package com.reuniondearte.api.article;

import java.time.OffsetDateTime;

public record ArticleDetailResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String contentHtml,
        String category,
        String author,
        String coverImage,
        String coverAlt,
        String coverCaption,
        String coverCredit,
        String coverImageUrl,
        String coverAltText,
        OffsetDateTime publishedAt,
        String canonicalUrl,
        Integer readingTimeMinutes
) {
    static ArticleDetailResponse from(Article article) {
        var category = article.getPrimaryCategory() == null ? null : article.getPrimaryCategory().getSlug();
        var author = article.getAuthor() == null ? null : article.getAuthor().getName();
        var cover = article.getCoverMedia();
        var coverUrl = cover == null ? null : cover.getPublicUrl();
        var coverAlt = cover == null ? null : cover.getAltText();
        var coverCaption = cover == null ? null : cover.getCaption();
        var coverCredit = cover == null ? null : cover.getCredit();
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getExcerpt(),
                article.getContentHtml(),
                category,
                author,
                coverUrl,
                coverAlt,
                coverCaption,
                coverCredit,
                coverUrl,
                coverAlt,
                article.getPublishedAt(),
                article.getCanonicalUrl(),
                article.getReadingTimeMinutes()
        );
    }
}
