package com.reuniondearte.api.interaction;

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
@Table(name = "article_comments")
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "public_name", nullable = false, length = 80)
    private String publicName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ArticleCommentStatus status = ArticleCommentStatus.PENDING;

    @Column(name = "consent_accepted", nullable = false)
    private boolean consentAccepted;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "rejected_at")
    private OffsetDateTime rejectedAt;

    @Column(name = "moderation_notes", columnDefinition = "TEXT")
    private String moderationNotes;

    protected ArticleComment() {
    }

    public ArticleComment(Article article, String publicName, String body, boolean consentAccepted) {
        this.article = article;
        this.publicName = publicName;
        this.body = body;
        this.consentAccepted = consentAccepted;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Article getArticle() {
        return article;
    }

    public String getPublicName() {
        return publicName;
    }

    public String getBody() {
        return body;
    }

    public ArticleCommentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void approve() {
        this.status = ArticleCommentStatus.APPROVED;
        this.approvedAt = OffsetDateTime.now();
        this.rejectedAt = null;
    }

    public void reject() {
        this.status = ArticleCommentStatus.REJECTED;
        this.rejectedAt = OffsetDateTime.now();
        this.approvedAt = null;
    }
}
