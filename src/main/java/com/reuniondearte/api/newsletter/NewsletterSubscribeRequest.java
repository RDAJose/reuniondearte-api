package com.reuniondearte.api.newsletter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewsletterSubscribeRequest(
        @NotBlank @Email String email,
        boolean consentAccepted,
        String website
) {
}
