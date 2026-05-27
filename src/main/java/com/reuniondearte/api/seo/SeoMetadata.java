package com.reuniondearte.api.seo;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.media.MediaAsset;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "seo_metadata")
public class SeoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    private String metaTitle;
    private String metaDescription;
    private String ogTitle;
    private String ogDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "og_image_media_id")
    private MediaAsset ogImage;

    private String twitterTitle;
    private String twitterDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "twitter_image_media_id")
    private MediaAsset twitterImage;

    @Column(columnDefinition = "TEXT")
    private String canonicalUrl;

    private Boolean noindex;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

