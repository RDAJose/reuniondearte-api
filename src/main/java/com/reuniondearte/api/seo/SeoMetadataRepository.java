package com.reuniondearte.api.seo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeoMetadataRepository extends JpaRepository<SeoMetadata, Long> {
    Optional<SeoMetadata> findByArticleId(Long articleId);
}
