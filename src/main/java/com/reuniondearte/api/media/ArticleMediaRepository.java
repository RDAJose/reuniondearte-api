package com.reuniondearte.api.media;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleMediaRepository extends JpaRepository<ArticleMedia, Long> {
    @EntityGraph(attributePaths = {"mediaAsset"})
    List<ArticleMedia> findByArticleIdAndRoleOrderBySortOrderAscIdAsc(Long articleId, String role);

    @EntityGraph(attributePaths = {"mediaAsset"})
    Optional<ArticleMedia> findByIdAndArticleIdAndRole(Long id, Long articleId, String role);

    int countByArticleIdAndRole(Long articleId, String role);

    void deleteByArticleIdAndRoleIn(Long articleId, List<String> roles);
}
