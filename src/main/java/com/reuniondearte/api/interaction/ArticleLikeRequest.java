package com.reuniondearte.api.interaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArticleLikeRequest(
        @NotBlank @Size(max = 200) String clientId,
        @NotNull Boolean liked
) {
}
