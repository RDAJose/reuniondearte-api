package com.reuniondearte.api.newsletter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "newsletter_subscribers")
public class NewsletterSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false, unique = true, length = 320)
    private String emailNormalized;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NewsletterSubscriberStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String consentText;

    @Column(nullable = false, length = 50)
    private String consentVersion;

    @Column(length = 128)
    private String confirmationTokenHash;

    @Column(nullable = false, length = 128)
    private String unsubscribeTokenHash;

    private OffsetDateTime confirmedAt;
    private OffsetDateTime unsubscribedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastEmailSentAt;

    @Column(length = 80)
    private String source;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailNormalized() {
        return emailNormalized;
    }

    public NewsletterSubscriberStatus getStatus() {
        return status;
    }

    public String getConsentText() {
        return consentText;
    }

    public String getConsentVersion() {
        return consentVersion;
    }

    public String getConfirmationTokenHash() {
        return confirmationTokenHash;
    }

    public String getUnsubscribeTokenHash() {
        return unsubscribeTokenHash;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public OffsetDateTime getUnsubscribedAt() {
        return unsubscribedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getLastEmailSentAt() {
        return lastEmailSentAt;
    }

    public String getSource() {
        return source;
    }

    public String getNotes() {
        return notes;
    }

    public void startPending(
            String email,
            String emailNormalized,
            String consentText,
            String consentVersion,
            String confirmationTokenHash,
            String unsubscribeTokenHash,
            String source
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        this.email = email;
        this.emailNormalized = emailNormalized;
        this.status = NewsletterSubscriberStatus.PENDING_CONFIRMATION;
        this.consentText = consentText;
        this.consentVersion = consentVersion;
        this.confirmationTokenHash = confirmationTokenHash;
        this.unsubscribeTokenHash = unsubscribeTokenHash;
        this.confirmedAt = null;
        this.unsubscribedAt = null;
        this.source = source;
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    public void confirm() {
        OffsetDateTime now = OffsetDateTime.now();
        this.status = NewsletterSubscriberStatus.ACTIVE;
        this.confirmationTokenHash = null;
        this.confirmedAt = now;
        this.unsubscribedAt = null;
        this.updatedAt = now;
    }

    public void unsubscribe() {
        OffsetDateTime now = OffsetDateTime.now();
        this.status = NewsletterSubscriberStatus.UNSUBSCRIBED;
        this.confirmationTokenHash = null;
        this.unsubscribedAt = now;
        this.updatedAt = now;
    }

    public void rotateUnsubscribeToken(String unsubscribeTokenHash) {
        this.unsubscribeTokenHash = unsubscribeTokenHash;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markEmailSent() {
        OffsetDateTime now = OffsetDateTime.now();
        this.lastEmailSentAt = now;
        this.updatedAt = now;
    }
}
