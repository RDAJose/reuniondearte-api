package com.reuniondearte.api.article;

import com.reuniondearte.api.author.AuthorResponse;
import java.time.OffsetDateTime;

public record ArticleDetailResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String contentMarkdown,
        String contentHtml,
        String category,
        String author,
        AuthorResponse authorDetails,
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
        var authorDetails = AuthorResponse.from(article.getAuthor());
        var author = authorDetails.name();
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
                article.getContentMarkdown(),
                ArticleContentRenderer.htmlFrom(article),
                category,
                author,
                authorDetails,
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
