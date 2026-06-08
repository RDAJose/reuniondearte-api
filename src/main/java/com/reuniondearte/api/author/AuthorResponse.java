package com.reuniondearte.api.author;

public record AuthorResponse(
        Long id,
        String name,
        String slug,
        String role,
        String bio,
        String avatarUrl
) {
    public static final String DEFAULT_NAME = "José Luis Olmedo Barrionuevo";
    public static final String DEFAULT_SLUG = "jose-luis-olmedo";
    public static final String DEFAULT_ROLE = "Creador, desarrollador y editor de Reunión de Arte";
    public static final String DEFAULT_BIO = "Creador, desarrollador y editor de Reunión de Arte.";

    public static AuthorResponse from(Author author) {
        if (author == null) {
            return defaultAuthor();
        }
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getSlug(),
                author.getRole(),
                author.getBio(),
                author.getAvatarUrl()
        );
    }

    public static AuthorResponse defaultAuthor() {
        return new AuthorResponse(null, DEFAULT_NAME, DEFAULT_SLUG, DEFAULT_ROLE, DEFAULT_BIO, null);
    }
}
