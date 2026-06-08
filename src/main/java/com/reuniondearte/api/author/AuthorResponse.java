package com.reuniondearte.api.author;

import com.reuniondearte.api.article.Article;
import java.util.Comparator;
import java.util.List;

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

    public static List<AuthorResponse> fromArticle(Article article) {
        if (article == null) {
            return List.of(defaultAuthor());
        }
        List<AuthorResponse> orderedAuthors = article.getArticleAuthors().stream()
                .sorted(Comparator.comparing(link -> link.getPosition() == null ? Integer.MAX_VALUE : link.getPosition()))
                .map(link -> from(link.getAuthor()))
                .toList();
        if (!orderedAuthors.isEmpty()) {
            return orderedAuthors;
        }
        if (article.getAuthor() != null) {
            return List.of(from(article.getAuthor()));
        }
        return List.of(defaultAuthor());
    }

    public static AuthorResponse primaryFromArticle(Article article) {
        return fromArticle(article).get(0);
    }
}
