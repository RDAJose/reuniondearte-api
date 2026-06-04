package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.article.Article;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NewsletterMailService {
    private final JavaMailSender mailSender;
    private final String username;
    private final String from;
    private final String fromName;
    private final String apiPublicUrl;
    private final String publicSiteUrl;

    public NewsletterMailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${spring.mail.username:}") String username,
            @Value("${rda.mail-from:}") String from,
            @Value("${rda.mail-from-name:Reunion de Arte}") String fromName,
            @Value("${rda.api-public-url:http://localhost:8080}") String apiPublicUrl,
            @Value("${rda.public-site-url:https://reuniondearte.com}") String publicSiteUrl
    ) {
        this.mailSender = mailSender.getIfAvailable();
        this.username = username;
        this.from = from;
        this.fromName = fromName;
        this.apiPublicUrl = trimTrailingSlash(apiPublicUrl);
        this.publicSiteUrl = trimTrailingSlash(publicSiteUrl);
    }

    public void sendConfirmation(NewsletterSubscriber subscriber, String confirmationToken, String unsubscribeToken) {
        String confirmUrl = apiPublicUrl + "/api/newsletter/confirm?token=" + confirmationToken;
        String unsubscribeUrl = apiPublicUrl + "/api/newsletter/unsubscribe?token=" + unsubscribeToken;
        String body = """
                Has solicitado suscribirte a la newsletter de Reunion de Arte.

                Confirma tu alta aqui:
                %s

                Si no has solicitado esta suscripcion, puedes ignorar este correo.

                Baja automatica:
                %s
                """.formatted(confirmUrl, unsubscribeUrl);
        send(subscriber.getEmail(), "Confirma tu suscripcion a Reunion de Arte", body);
    }

    public void sendArticleNotice(NewsletterSubscriber subscriber, Article article, String unsubscribeToken) {
        String articleUrl = publicSiteUrl + "/articulos/" + article.getSlug();
        String unsubscribeUrl = apiPublicUrl + "/api/newsletter/unsubscribe?token=" + unsubscribeToken;
        String subject = articleSubject(article);
        String body = """
                Nuevo articulo en Reunion de Arte

                %s

                %s

                Leer articulo:
                %s

                Baja automatica:
                %s
                """.formatted(article.getTitle(), article.getExcerpt() == null ? "" : article.getExcerpt(), articleUrl, unsubscribeUrl);
        send(subscriber.getEmail(), subject, body);
    }

    public String articleSubject(Article article) {
        return "Nuevo articulo en Reunion de Arte: " + article.getTitle();
    }

    private void send(String to, String subject, String body) {
        if (mailSender == null || !StringUtils.hasText(username)) {
            throw new IllegalStateException("SMTP is not configured. Set RDA_MAIL_HOST, RDA_MAIL_USERNAME, RDA_MAIL_PASSWORD and RDA_MAIL_FROM.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        String sender = StringUtils.hasText(from) ? from : username;
        message.setFrom(StringUtils.hasText(fromName) ? fromName + " <" + sender + ">" : sender);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new IllegalStateException("Email could not be sent", ex);
        }
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
