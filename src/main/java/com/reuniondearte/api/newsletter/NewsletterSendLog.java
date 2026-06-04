package com.reuniondearte.api.newsletter;

import com.reuniondearte.api.article.Article;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "newsletter_send_logs")
public class NewsletterSendLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, length = 128)
    private String recipientEmailHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NewsletterSendStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private OffsetDateTime sentAt;

    public static NewsletterSendLog of(Article article, String subject, String recipientEmailHash, NewsletterSendStatus status, String errorMessage) {
        NewsletterSendLog log = new NewsletterSendLog();
        log.article = article;
        log.subject = subject;
        log.recipientEmailHash = recipientEmailHash;
        log.status = status;
        log.errorMessage = errorMessage;
        log.sentAt = OffsetDateTime.now();
        return log;
    }
}
