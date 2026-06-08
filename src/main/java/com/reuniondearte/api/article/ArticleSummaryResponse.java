package com.reuniondearte.api.article;

import com.reuniondearte.api.author.AuthorResponse;
import java.time.OffsetDateTime;
import java.util.List;

public record ArticleSummaryResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String category,
        String author,
        AuthorResponse authorDetails,
        List<AuthorResponse> authors,
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
    public static ArticleSummaryResponse from(Article article) {
        var category = article.getPrimaryCategory() == null ? null : article.getPrimaryCategory().getSlug();
        var authors = AuthorResponse.fromArticle(article);
        var authorDetails = authors.get(0);
        var author = authorDetails.name();
        var cover = article.getCoverMedia();
        var coverUrl = cover == null ? null : cover.getPublicUrl();
        var coverAlt = cover == null ? null : cover.getAltText();
        var coverCaption = cover == null ? null : cover.getCaption();
        var coverCredit = cover == null ? null : cover.getCredit();
        return new ArticleSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getExcerpt(),
                category,
                author,
                authorDetails,
                authors,
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
