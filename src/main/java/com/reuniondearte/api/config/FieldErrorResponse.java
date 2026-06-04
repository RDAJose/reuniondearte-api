package com.reuniondearte.api.config;

public record FieldErrorResponse(
        String field,
        String message
) {
}
