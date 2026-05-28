package com.reuniondearte.api.interaction;

public record ArticleLikeResponse(
        boolean liked,
        long likeCount
) {
}
