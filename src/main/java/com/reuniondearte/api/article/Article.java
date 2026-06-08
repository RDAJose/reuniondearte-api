package com.reuniondearte.api.article;

import com.reuniondearte.api.author.Author;
import com.reuniondearte.api.category.Category;
import com.reuniondearte.api.media.MediaAsset;
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
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 260)
    private String title;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "content_html", columnDefinition = "TEXT")
    private String contentHtml;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ArticleStatus status = ArticleStatus.draft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_category_id")
    private Category primaryCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_media_id")
    private MediaAsset coverMedia;

    private OffsetDateTime publishedAt;
    private OffsetDateTime scheduledAt;
    private String importSource;

    @Column(columnDefinition = "TEXT")
    private String importOriginalPath;

    @Column(columnDefinition = "TEXT")
    private String canonicalUrl;

    private Integer readingTimeMinutes;
    private String language = "es";
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public Author getAuthor() {
        return author;
    }

    public Category getPrimaryCategory() {
        return primaryCategory;
    }

    public MediaAsset getCoverMedia() {
        return coverMedia;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getImportOriginalPath() {
        return importOriginalPath;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public Integer getReadingTimeMinutes() {
        return readingTimeMinutes;
    }

    public void applyEditorialUpdate(
            String title,
            String slug,
            String excerpt,
            String contentMarkdown,
            ArticleStatus status,
            Author author,
            Category primaryCategory,
            OffsetDateTime publishedAt,
            String canonicalUrl
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        this.title = title;
        this.slug = slug;
        this.excerpt = excerpt;
        this.contentMarkdown = contentMarkdown;
        this.contentHtml = null;
        this.status = status;
        this.author = author;
        this.primaryCategory = primaryCategory;
        this.publishedAt = status == ArticleStatus.published ? publishedAt : null;
        this.scheduledAt = null;
        this.canonicalUrl = canonicalUrl;
        this.readingTimeMinutes = calculateReadingTime(contentMarkdown);
        this.language = "es";
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    public void changeStatus(ArticleStatus status) {
        this.status = status;
        if (status != ArticleStatus.published) {
            this.publishedAt = null;
        } else if (this.publishedAt == null) {
            this.publishedAt = OffsetDateTime.now();
        }
        this.updatedAt = OffsetDateTime.now();
    }

    public void publishNow() {
        this.status = ArticleStatus.published;
        if (this.publishedAt == null) {
            this.publishedAt = OffsetDateTime.now();
        }
        this.updatedAt = OffsetDateTime.now();
    }

    public void moveToDraft() {
        this.status = ArticleStatus.draft;
        this.publishedAt = null;
        this.scheduledAt = null;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateCoverMedia(MediaAsset coverMedia) {
        this.coverMedia = coverMedia;
        this.updatedAt = OffsetDateTime.now();
    }

    public void applyLegacyDraftImport(
            String title,
            String slug,
            String excerpt,
            String contentMarkdown,
            Category primaryCategory,
            String importSource,
            String importOriginalPath,
            Integer readingTimeMinutes
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        this.title = title;
        this.slug = slug;
        this.excerpt = excerpt;
        this.contentMarkdown = contentMarkdown;
        this.contentHtml = null;
        this.status = ArticleStatus.draft;
        this.primaryCategory = primaryCategory;
        this.publishedAt = null;
        this.scheduledAt = null;
        this.importSource = importSource;
        this.importOriginalPath = importOriginalPath;
        this.readingTimeMinutes = readingTimeMinutes;
        this.language = "es";
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    public void normalizeImportedContent(String contentMarkdown, boolean clearContentHtml) {
        this.contentMarkdown = contentMarkdown;
        if (clearContentHtml) {
            this.contentHtml = null;
        }
        this.readingTimeMinutes = calculateReadingTime(contentMarkdown);
        this.updatedAt = OffsetDateTime.now();
    }

    private Integer calculateReadingTime(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }
        int words = content.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(words / 220.0));
    }
}
