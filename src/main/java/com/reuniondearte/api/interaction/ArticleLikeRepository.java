package com.reuniondearte.api.interaction;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    long countByArticleId(Long articleId);

    Optional<ArticleLike> findByArticleIdAndVisitorKeyHash(Long articleId, String visitorKeyHash);
}
