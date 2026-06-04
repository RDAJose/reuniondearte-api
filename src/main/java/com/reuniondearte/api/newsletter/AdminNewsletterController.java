package com.reuniondearte.api.newsletter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/newsletter")
public class AdminNewsletterController {
    private final NewsletterService newsletter;

    public AdminNewsletterController(NewsletterService newsletter) {
        this.newsletter = newsletter;
    }

    @GetMapping("/subscribers")
    public AdminNewsletterSummaryResponse subscribers(
            @RequestParam(required = false) NewsletterSubscriberStatus status,
            @RequestParam(defaultValue = "200") int limit
    ) {
        return newsletter.subscribers(status, limit);
    }

    @GetMapping("/export.csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"newsletter-subscribers.csv\"");
        response.getWriter().println("email,status,created_at,confirmed_at,unsubscribed_at,last_email_sent_at,source,consent_version");
        for (NewsletterSubscriber subscriber : newsletter.exportSubscribers()) {
            response.getWriter().println(String.join(",",
                    csv(subscriber.getEmail()),
                    csv(subscriber.getStatus().name()),
                    csv(subscriber.getCreatedAt()),
                    csv(subscriber.getConfirmedAt()),
                    csv(subscriber.getUnsubscribedAt()),
                    csv(subscriber.getLastEmailSentAt()),
                    csv(subscriber.getSource()),
                    csv(subscriber.getConsentVersion())
            ));
        }
    }

    @PostMapping(value = "/articles/{articleId}/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AdminNewsletterSendResponse sendArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody AdminNewsletterSendRequest request
    ) {
        return newsletter.sendArticleNotice(articleId, request);
    }

    private String csv(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
