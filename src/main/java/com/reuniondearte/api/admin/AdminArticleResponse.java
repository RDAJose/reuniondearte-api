package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.seo.SeoMetadata;

import java.time.OffsetDateTime;
import java.util.List;

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
        Boolean noindex,
        AdminArticleCoverResponse cover,
        List<AdminArticleMediaResponse> bodyImages,
        List<AdminArticleMediaFileResponse> mediaFiles
) {
    public static AdminArticleResponse from(Article article, SeoMetadata seo) {
        return from(article, seo, List.of(), List.of());
    }

    public static AdminArticleResponse from(Article article, SeoMetadata seo, List<AdminArticleMediaResponse> bodyImages) {
        return from(article, seo, bodyImages, List.of());
    }

    public static AdminArticleResponse from(
            Article article,
            SeoMetadata seo,
            List<AdminArticleMediaResponse> bodyImages,
            List<AdminArticleMediaFileResponse> mediaFiles
    ) {
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
                seo != null && Boolean.TRUE.equals(seo.getNoindex()),
                article.getCoverMedia() == null ? null : AdminArticleCoverResponse.from(article, article.getCoverMedia()),
                bodyImages,
                mediaFiles
        );
    }
}
