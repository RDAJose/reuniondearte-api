package com.reuniondearte.api.newsletter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reuniondearte.api.article.Article;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NewsletterMailService {
    private static final Logger log = LoggerFactory.getLogger(NewsletterMailService.class);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String provider;
    private final String brevoApiKey;
    private final String brevoApiUrl;
    private final String from;
    private final String fromName;
    private final String apiPublicUrl;
    private final String publicSiteUrl;
    private final Duration timeout;

    public NewsletterMailService(
            ObjectMapper objectMapper,
            @Value("${rda.mail-provider:disabled}") String provider,
            @Value("${rda.brevo-api-key:}") String brevoApiKey,
            @Value("${rda.brevo-api-url:https://api.brevo.com/v3/smtp/email}") String brevoApiUrl,
            @Value("${rda.mail-from:}") String from,
            @Value("${rda.mail-from-name:Reunion de Arte}") String fromName,
            @Value("${rda.api-public-url:http://localhost:8080}") String apiPublicUrl,
            @Value("${rda.public-site-url:https://reuniondearte.com}") String publicSiteUrl,
            @Value("${rda.mail-timeout-ms:10000}") long timeoutMs
    ) {
        this.objectMapper = objectMapper;
        this.provider = provider == null ? "disabled" : provider.trim().toLowerCase();
        this.brevoApiKey = brevoApiKey;
        this.brevoApiUrl = brevoApiUrl;
        this.from = from;
        this.fromName = fromName;
        this.apiPublicUrl = trimTrailingSlash(apiPublicUrl);
        this.publicSiteUrl = trimTrailingSlash(publicSiteUrl);
        this.timeout = Duration.ofMillis(Math.max(1000, timeoutMs));
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
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

                Si lo recibes, la configuracion Brevo Transactional Email esta funcionando.
                """;
        send(to, "Prueba newsletter Reunion de Arte", body);
    }

    public String articleSubject(Article article) {
        return "Nuevo articulo en Reunion de Arte: " + article.getTitle();
    }

    private void send(String to, String subject, String textBody) {
        if (!"brevo".equals(provider)) {
            throw unavailable("mail provider is disabled or unsupported: " + provider);
        }
        validateBrevoConfiguration();
        sendWithBrevo(to, subject, textBody);
    }

    private void sendWithBrevo(String to, String subject, String textBody) {
        String requestBody = brevoPayload(to, subject, textBody);
        HttpRequest request = HttpRequest.newBuilder(URI.create(brevoApiUrl))
                .timeout(timeout)
                .header("api-key", brevoApiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String message = "Brevo API returned HTTP " + response.statusCode() + ": " + cleanMessage(response.body());
                log.warn("newsletter brevo send failed status={} message={}", response.statusCode(), cleanMessage(response.body()));
                throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + message);
            }
        } catch (IOException ex) {
            log.warn("newsletter brevo send failed type={} message={}", ex.getClass().getSimpleName(), cleanMessage(ex.getMessage()));
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + cleanMessage(ex.getMessage()), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("newsletter brevo send interrupted type={} message={}", ex.getClass().getSimpleName(), cleanMessage(ex.getMessage()));
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: Brevo API request was interrupted", ex);
        } catch (IllegalArgumentException ex) {
            log.warn("newsletter brevo request invalid type={} message={}", ex.getClass().getSimpleName(), cleanMessage(ex.getMessage()));
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + cleanMessage(ex.getMessage()), ex);
        }
    }

    private String brevoPayload(String to, String subject, String textBody) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("email", from, "name", fromName),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "textContent", textBody
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: email payload could not be built", ex);
        }
    }

    private void validateBrevoConfiguration() {
        if (!StringUtils.hasText(brevoApiKey)) {
            throw unavailable("missing RDA_BREVO_API_KEY");
        }
        if (!StringUtils.hasText(brevoApiUrl)) {
            throw unavailable("missing RDA_BREVO_API_URL");
        }
        if (!StringUtils.hasText(from)) {
            throw unavailable("missing RDA_MAIL_FROM");
        }
        if (!StringUtils.hasText(fromName)) {
            throw unavailable("missing RDA_MAIL_FROM_NAME");
        }
    }

    private EmailServiceUnavailableException unavailable(String reason) {
        log.warn("newsletter mail configuration unavailable provider={} reason={}", provider, reason);
        return new EmailServiceUnavailableException("Email service is not configured or could not send confirmation: " + reason);
    }

    private String cleanMessage(String message) {
        if (message == null || message.isBlank()) {
            return "unknown error";
        }
        String cleaned = message
                .replaceAll("(?i)(api-key|x-sib-api-key|authorization)[=:]\\s*[^\\s,;\\}]+", "$1=***")
                .replaceAll("(?i)(password|pass)[=:]\\s*[^\\s,;\\}]+", "$1=***");
        return cleaned.length() > 300 ? cleaned.substring(0, 300) : cleaned;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
