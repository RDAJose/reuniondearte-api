package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.article.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NewsletterMailService {
    private static final Logger log = LoggerFactory.getLogger(NewsletterMailService.class);

    private final JavaMailSender mailSender;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String from;
    private final String fromName;
    private final String apiPublicUrl;
    private final String publicSiteUrl;

    public NewsletterMailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.port:587}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${rda.mail-from:}") String from,
            @Value("${rda.mail-from-name:Reunion de Arte}") String fromName,
            @Value("${rda.api-public-url:http://localhost:8080}") String apiPublicUrl,
            @Value("${rda.public-site-url:https://reuniondearte.com}") String publicSiteUrl
    ) {
        this.mailSender = mailSender.getIfAvailable();
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
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

    public void sendTestMail(String to) {
        String body = """
                Este es un email de prueba del sistema de newsletter de Reunion de Arte.

                Si lo recibes, la configuracion SMTP esta funcionando.
                """;
        send(to, "Prueba newsletter Reunión de Arte", body);
    }

    public String articleSubject(Article article) {
        return "Nuevo articulo en Reunion de Arte: " + article.getTitle();
    }

    private void send(String to, String subject, String body) {
        validateConfiguration();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromName + " <" + from + ">");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("newsletter smtp send failed type={} message={}", ex.getClass().getSimpleName(), cleanMessage(ex));
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + cleanMessage(ex), ex);
        } catch (RuntimeException ex) {
            log.warn("newsletter smtp runtime failure type={} message={}", ex.getClass().getSimpleName(), cleanMessage(ex));
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + cleanMessage(ex), ex);
        }
    }

    private void validateConfiguration() {
        if (mailSender == null) {
            throw unavailable("JavaMailSender bean is not available");
        }
        if (!StringUtils.hasText(host)) {
            throw unavailable("missing RDA_MAIL_HOST");
        }
        if (port <= 0) {
            throw unavailable("invalid RDA_MAIL_PORT");
        }
        if (!StringUtils.hasText(username)) {
            throw unavailable("missing RDA_MAIL_USERNAME");
        }
        if (!StringUtils.hasText(password)) {
            throw unavailable("missing RDA_MAIL_PASSWORD");
        }
        if (!StringUtils.hasText(from)) {
            throw unavailable("missing RDA_MAIL_FROM");
        }
        if (!StringUtils.hasText(fromName)) {
            throw unavailable("missing RDA_MAIL_FROM_NAME");
        }
    }

    private EmailServiceUnavailableException unavailable(String reason) {
        log.warn("newsletter smtp configuration unavailable reason={}", reason);
        return new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + reason);
    }

    private String cleanMessage(Throwable ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = ex.getClass().getSimpleName();
        }
        String cleaned = message
                .replaceAll("(?i)password=[^\\s,;]+", "password=***")
                .replaceAll("(?i)pass=[^\\s,;]+", "pass=***")
                .replaceAll("(?i)AUTH\\s+[^\\s]+", "AUTH ***");
        return cleaned.length() > 300 ? cleaned.substring(0, 300) : cleaned;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
