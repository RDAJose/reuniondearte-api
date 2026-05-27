package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.seo.SeoMetadata;

import java.time.OffsetDateTime;

public record AdminArticleResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        @JsonProperty("content_markdown") String contentMarkdown,
        String category,
        String status,
        @JsonProperty("published_at") OffsetDateTime publishedAt,
        @JsonProperty("canonical_url") String canonicalUrl,
        @JsonProperty("meta_title") String metaTitle,
        @JsonProperty("meta_description") String metaDescription,
        Boolean noindex
) {
    public static AdminArticleResponse from(Article article, SeoMetadata seo) {
        return new AdminArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getExcerpt(),
                article.getContentMarkdown(),
                article.getPrimaryCategory() == null ? null : article.getPrimaryCategory().getSlug(),
                article.getStatus().name(),
                article.getPublishedAt(),
                article.getCanonicalUrl(),
                seo == null ? null : seo.getMetaTitle(),
                seo == null ? null : seo.getMetaDescription(),
                seo != null && Boolean.TRUE.equals(seo.getNoindex())
        );
    }
}
