package com.reuniondearte.api.interaction;

import com.reuniondearte.api.article.Article;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "article_likes",
        uniqueConstraints = @UniqueConstraint(name = "uq_article_likes_article_visitor", columnNames = {"article_id", "visitor_key_hash"})
)
public class ArticleLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "visitor_key_hash", nullable = false, length = 64)
    private String visitorKeyHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ArticleLike() {
    }

    public ArticleLike(Article article, String visitorKeyHash) {
        this.article = article;
        this.visitorKeyHash = visitorKeyHash;
        this.createdAt = OffsetDateTime.now();
    }
}
