package com.reuniondearte.api.article;

import java.time.OffsetDateTime;

public record ArticleSummaryResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String category,
        String author,
        String coverImageUrl,
        String coverAltText,
        OffsetDateTime publishedAt,
        String canonicalUrl,
        Integer readingTimeMinutes
) {
    public static ArticleSummaryResponse from(Article article) {
        var category = article.getPrimaryCategory() == null ? null : article.getPrimaryCategory().getSlug();
        var author = article.getAuthor() == null ? null : article.getAuthor().getName();
        var coverUrl = article.getCoverMedia() == null ? null : article.getCoverMedia().getPublicUrl();
        var coverAlt = article.getCoverMedia() == null ? null : article.getCoverMedia().getAltText();
        return new ArticleSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getExcerpt(),
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
