package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.article.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record AdminArticleRequest(
        @NotBlank @Size(max = 260) String title,
        @NotBlank @Size(max = 280) String slug,
        String excerpt,
        @JsonProperty("content_markdown") String contentMarkdown,
        String category,
        ArticleStatus status,
        @JsonProperty("published_at") OffsetDateTime publishedAt,
        @JsonProperty("canonical_url") String canonicalUrl,
        @JsonProperty("meta_title") String metaTitle,
        @JsonProperty("meta_description") String metaDescription,
        Boolean noindex
) {
}
