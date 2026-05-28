package com.reuniondearte.api.interaction;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    long countByArticleIdAndStatus(Long articleId, ArticleCommentStatus status);

    List<ArticleComment> findByArticleIdAndStatusOrderByCreatedAtDesc(Long articleId, ArticleCommentStatus status);

    @EntityGraph(attributePaths = {"article"})
    List<ArticleComment> findByStatusOrderByCreatedAtDesc(ArticleCommentStatus status);

    @EntityGraph(attributePaths = {"article"})
    List<ArticleComment> findAllByOrderByCreatedAtDesc();
}
