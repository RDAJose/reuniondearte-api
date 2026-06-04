package com.reuniondearte.api.newsletter;

import java.time.OffsetDateTime;

public record AdminNewsletterSubscriberResponse(
        Long id,
        String email,
        NewsletterSubscriberStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime confirmedAt,
        OffsetDateTime unsubscribedAt,
        OffsetDateTime lastEmailSentAt,
        String source,
        String consentVersion
) {
    public static AdminNewsletterSubscriberResponse from(NewsletterSubscriber subscriber) {
        return new AdminNewsletterSubscriberResponse(
                subscriber.getId(),
                subscriber.getEmail(),
                subscriber.getStatus(),
                subscriber.getCreatedAt(),
                subscriber.getConfirmedAt(),
                subscriber.getUnsubscribedAt(),
                subscriber.getLastEmailSentAt(),
                subscriber.getSource(),
                subscriber.getConsentVersion()
        );
    }
}
