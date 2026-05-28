package com.reuniondearte.api.media;

import com.reuniondearte.api.article.Article;
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
@Table(name = "article_media")
public class ArticleMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_asset_id", nullable = false)
    private MediaAsset mediaAsset;

    private String role;
    private Integer sortOrder;
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Article getArticle() {
        return article;
    }

    public MediaAsset getMediaAsset() {
        return mediaAsset;
    }

    public String getRole() {
        return role;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public static ArticleMedia create(Article article, MediaAsset mediaAsset, String role, Integer sortOrder) {
        ArticleMedia articleMedia = new ArticleMedia();
        articleMedia.article = article;
        articleMedia.mediaAsset = mediaAsset;
        articleMedia.role = role;
        articleMedia.sortOrder = sortOrder == null ? 0 : sortOrder;
        articleMedia.createdAt = OffsetDateTime.now();
        return articleMedia;
    }
}
