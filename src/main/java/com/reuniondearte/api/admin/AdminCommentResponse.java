package com.reuniondearte.api.admin;

import com.reuniondearte.api.interaction.ArticleComment;
import java.time.OffsetDateTime;

public record AdminCommentResponse(
        Long id,
        String article,
        String slug,
        String publicName,
        String body,
        OffsetDateTime createdAt,
        String status
) {
    public static AdminCommentResponse from(ArticleComment comment) {
        return new AdminCommentResponse(
                comment.getId(),
                comment.getArticle().getTitle(),
                comment.getArticle().getSlug(),
                comment.getPublicName(),
                comment.getBody(),
                comment.getCreatedAt(),
                comment.getStatus().name()
        );
    }
}
