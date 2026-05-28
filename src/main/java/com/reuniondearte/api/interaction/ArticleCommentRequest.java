package com.reuniondearte.api.interaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArticleCommentRequest(
        @NotBlank @Size(max = 80) String publicName,
        @NotBlank @Size(max = 1500) String body,
        @NotNull Boolean consentAccepted,
        String website
) {
}
