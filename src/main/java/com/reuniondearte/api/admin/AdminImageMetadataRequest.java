package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AdminImageMetadataRequest(
        @JsonProperty("altText") @NotBlank String altText,
        String caption,
        String credit,
        String sourceUrl,
        String rightsNotes
) {
}
