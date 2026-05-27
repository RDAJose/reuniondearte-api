package com.reuniondearte.api.article;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @EntityGraph(attributePaths = {"author", "primaryCategory", "coverMedia"})
    List<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status);

    @EntityGraph(attributePaths = {"author", "primaryCategory", "coverMedia"})
    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    Optional<Article> findBySlug(String slug);

    @EntityGraph(attributePaths = {"author", "primaryCategory", "coverMedia"})
    List<Article> findByPrimaryCategorySlugAndStatusOrderByPublishedAtDesc(String slug, ArticleStatus status);
}
