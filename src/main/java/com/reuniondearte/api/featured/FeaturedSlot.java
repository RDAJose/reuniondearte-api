package com.reuniondearte.api.featured;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.category.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "featured_slots")
public class FeaturedSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slotKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer sortOrder;
    private Boolean active;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public Article getArticle() {
        return article;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}

