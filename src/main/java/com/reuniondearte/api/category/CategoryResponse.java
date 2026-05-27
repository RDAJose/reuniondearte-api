package com.reuniondearte.api.category;

public record CategoryResponse(Long id, String name, String slug, String description, Integer sortOrder) {
    static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getSortOrder()
        );
    }
}

