package com.reuniondearte.api.interaction;

import java.time.OffsetDateTime;

public record ArticleCommentResponse(
        Long id,
        String publicName,
        String body,
        OffsetDateTime createdAt
) {
    public static ArticleCommentResponse from(ArticleComment comment) {
        return new ArticleCommentResponse(
                comment.getId(),
                comment.getPublicName(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}
