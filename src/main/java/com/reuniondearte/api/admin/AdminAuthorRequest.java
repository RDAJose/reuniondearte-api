package com.reuniondearte.api.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminAuthorRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 180, message = "Name must be 180 characters or fewer")
        String name,

        @NotBlank(message = "Slug is required")
        @Size(max = 220, message = "Slug must be 220 characters or fewer")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)+$", message = "Slug must be lowercase, without accents or spaces, and include hyphens")
        String slug,

        @NotBlank(message = "Role is required")
        @Size(max = 260, message = "Role must be 260 characters or fewer")
        String role,

        String bio,

        @SafeAuthorUrl(kind = SafeAuthorUrl.Kind.AVATAR, message = "Avatar URL must be empty, a safe absolute http(s) URL, or a file under /authors/")
        String avatarUrl,

        @SafeAuthorUrl(kind = SafeAuthorUrl.Kind.EXTERNAL, message = "Website URL must be empty or a safe absolute http(s) URL")
        String websiteUrl,

        @SafeAuthorUrl(kind = SafeAuthorUrl.Kind.EXTERNAL, message = "Letterboxd URL must be empty or a safe absolute http(s) URL")
        String letterboxdUrl
) {
    public AdminAuthorRequest {
        name = trim(name);
        slug = trim(slug);
        role = trim(role);
        bio = trim(bio);
        avatarUrl = trim(avatarUrl);
        websiteUrl = trim(websiteUrl);
        letterboxdUrl = trim(letterboxdUrl);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
