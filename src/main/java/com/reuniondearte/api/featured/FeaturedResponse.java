package com.reuniondearte.api.featured;

import com.reuniondearte.api.article.ArticleSummaryResponse;

public record FeaturedResponse(Long id, String slotKey, Integer sortOrder, ArticleSummaryResponse article) {
    static FeaturedResponse from(FeaturedSlot slot) {
        return new FeaturedResponse(
                slot.getId(),
                slot.getSlotKey(),
                slot.getSortOrder(),
                ArticleSummaryResponse.from(slot.getArticle())
        );
    }
}

