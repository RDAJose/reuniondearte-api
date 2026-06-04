package com.reuniondearte.api.newsletter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminNewsletterTestMailRequest(
        @NotBlank @Email String email
) {
}
