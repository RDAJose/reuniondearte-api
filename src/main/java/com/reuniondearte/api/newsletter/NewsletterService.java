package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NewsletterService {
    private static final String CONSENT_VERSION = "2026-06-04";
    private static final String CONSENT_TEXT = "Acepto recibir por email comunicaciones editoriales de Reunion de Arte y entiendo que puedo darme de baja en cualquier momento.";
    private static final String SOURCE_PUBLIC_FORM = "public_form";
    private static final String CONFIRM_SEND = "ENVIAR NEWSLETTER";

    private final NewsletterSubscriberRepository subscribers;
    private final NewsletterSendLogRepository sendLogs;
    private final ArticleRepository articles;
    private final NewsletterTokenService tokens;
    private final NewsletterMailService mail;

    public NewsletterService(
            NewsletterSubscriberRepository subscribers,
            NewsletterSendLogRepository sendLogs,
            ArticleRepository articles,
            NewsletterTokenService tokens,
            NewsletterMailService mail
    ) {
        this.subscribers = subscribers;
        this.sendLogs = sendLogs;
        this.articles = articles;
        this.tokens = tokens;
        this.mail = mail;
    }

    @Transactional
    public NewsletterSubscribeResponse subscribe(NewsletterSubscribeRequest request) {
        if (request.website() != null && !request.website().isBlank()) {
            return neutralResponse();
        }
        if (!request.consentAccepted()) {
            throw new ConsentRequiredException();
        }
        String email = request.email().trim();
        String normalized = normalizeEmail(email);
        String confirmationToken = tokens.newToken();
        String unsubscribeToken = tokens.newToken();
        NewsletterSubscriber subscriber = subscribers.findByEmailNormalized(normalized).orElseGet(NewsletterSubscriber::new);
        if (subscriber.getStatus() == NewsletterSubscriberStatus.ACTIVE) {
            return neutralResponse();
        }
        subscriber.startPending(
                email,
                normalized,
                CONSENT_TEXT,
                CONSENT_VERSION,
                tokens.hash(confirmationToken),
                tokens.hash(unsubscribeToken),
                SOURCE_PUBLIC_FORM
        );
        NewsletterSubscriber saved = subscribers.save(subscriber);
        try {
            mail.sendConfirmation(saved, confirmationToken, unsubscribeToken);
        } catch (EmailServiceUnavailableException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new EmailServiceUnavailableException();
        }
        return neutralResponse();
    }

    @Transactional
    public boolean confirm(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return subscribers.findByConfirmationTokenHash(tokens.hash(token.trim()))
                .map(subscriber -> {
                    subscriber.confirm();
                    subscribers.save(subscriber);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean unsubscribe(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return subscribers.findByUnsubscribeTokenHash(tokens.hash(token.trim()))
                .map(subscriber -> {
                    subscriber.unsubscribe();
                    subscribers.save(subscriber);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public AdminNewsletterSummaryResponse subscribers(NewsletterSubscriberStatus status, int limit) {
        NewsletterSubscriberStatus selectedStatus = status == null ? NewsletterSubscriberStatus.ACTIVE : status;
        int safeLimit = Math.max(1, Math.min(limit, 500));
        Map<NewsletterSubscriberStatus, Long> totals = new EnumMap<>(NewsletterSubscriberStatus.class);
        for (NewsletterSubscriberStatus value : NewsletterSubscriberStatus.values()) {
            totals.put(value, subscribers.countByStatus(value));
        }
        List<AdminNewsletterSubscriberResponse> list = subscribers.findByStatusOrderByCreatedAtDesc(selectedStatus, PageRequest.of(0, safeLimit)).stream()
                .map(AdminNewsletterSubscriberResponse::from)
                .toList();
        return new AdminNewsletterSummaryResponse(totals, list);
    }

    @Transactional(readOnly = true)
    public List<NewsletterSubscriber> exportSubscribers() {
        return subscribers.findAll().stream()
                .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                .toList();
    }

    @Transactional
    public AdminNewsletterSendResponse sendArticleNotice(Long articleId, AdminNewsletterSendRequest request) {
        if (request == null || !CONFIRM_SEND.equals(request.confirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Strong confirmation is required");
        }
        Article article = articles.findWithRelationsById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        if (article.getStatus() != ArticleStatus.published) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only published articles can be sent");
        }

        String subject = truncate(mail.articleSubject(article), 255);
        int sent = 0;
        int failed = 0;
        int skipped = 0;
        Set<String> seen = new LinkedHashSet<>();
        for (NewsletterSubscriber subscriber : subscribers.findByStatusOrderByCreatedAtAsc(NewsletterSubscriberStatus.ACTIVE)) {
            if (!seen.add(subscriber.getEmailNormalized())) {
                skipped++;
                sendLogs.save(NewsletterSendLog.of(article, subject, tokens.hash(subscriber.getEmailNormalized()), NewsletterSendStatus.SKIPPED, "Duplicate recipient in operation"));
                continue;
            }
            String unsubscribeToken = tokens.newToken();
            String previousUnsubscribeTokenHash = subscriber.getUnsubscribeTokenHash();
            subscriber.rotateUnsubscribeToken(tokens.hash(unsubscribeToken));
            try {
                mail.sendArticleNotice(subscriber, article, unsubscribeToken);
                subscriber.markEmailSent();
                subscribers.save(subscriber);
                sendLogs.save(NewsletterSendLog.of(article, subject, tokens.hash(subscriber.getEmailNormalized()), NewsletterSendStatus.SENT, null));
                sent++;
            } catch (RuntimeException ex) {
                subscriber.rotateUnsubscribeToken(previousUnsubscribeTokenHash);
                subscribers.save(subscriber);
                sendLogs.save(NewsletterSendLog.of(article, subject, tokens.hash(subscriber.getEmailNormalized()), NewsletterSendStatus.FAILED, cleanError(ex)));
                failed++;
            }
        }
        return new AdminNewsletterSendResponse(sent, failed, skipped);
    }

    private NewsletterSubscribeResponse neutralResponse() {
        return new NewsletterSubscribeResponse("Si el email puede suscribirse, recibira un correo de confirmacion.");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String cleanError(RuntimeException ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    private String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
