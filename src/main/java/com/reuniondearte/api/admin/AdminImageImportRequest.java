package com.reuniondearte.api.admin;

import jakarta.validation.constraints.NotBlank;

public record AdminImageImportRequest(
        @NotBlank String imageUrl,
        @NotBlank String altText,
        @NotBlank String caption,
        @NotBlank String credit,
        @NotBlank String sourceUrl,
        @NotBlank String rightsNotes
) {
}
