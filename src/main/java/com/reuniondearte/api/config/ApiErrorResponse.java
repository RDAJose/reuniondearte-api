package com.reuniondearte.api.config;

import java.util.List;

public record ApiErrorResponse(
        String error,
        String message,
        List<FieldErrorResponse> fields
) {
    public static ApiErrorResponse of(String error, String message) {
        return new ApiErrorResponse(error, message, List.of());
    }
}
