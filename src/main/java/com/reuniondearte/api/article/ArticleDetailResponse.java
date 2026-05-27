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
        String coverImageUrl,
        String coverAltText,
        OffsetDateTime publishedAt,
        String canonicalUrl,
        Integer readingTimeMinutes
) {
    static ArticleDetailResponse from(Article article) {
        var category = article.getPrimaryCategory() == null ? null : article.getPrimaryCategory().getSlug();
        var author = article.getAuthor() == null ? null : article.getAuthor().getName();
        var coverUrl = article.getCoverMedia() == null ? null : article.getCoverMedia().getPublicUrl();
        var coverAlt = article.getCoverMedia() == null ? null : article.getCoverMedia().getAltText();
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
                article.getPublishedAt(),
                article.getCanonicalUrl(),
                article.getReadingTimeMinutes()
        );
    }
}

