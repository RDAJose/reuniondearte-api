package com.reuniondearte.api.importer.legacy;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import jakarta.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class ReviewedArticlePublisher {
    private static final Logger log = LoggerFactory.getLogger(ReviewedArticlePublisher.class);

    private final ArticleRepository articles;

    ReviewedArticlePublisher(ArticleRepository articles) {
        this.articles = articles;
    }

    @Transactional
    PublishResult publish(List<Long> requestedIds, boolean confirmedAllReview) {
        List<Article> candidates = requestedIds.isEmpty()
                ? articles.findByStatusOrderByUpdatedAtDesc(ArticleStatus.review)
                : articles.findAllById(new LinkedHashSet<>(requestedIds));
        Set<Long> requested = new LinkedHashSet<>(requestedIds);
        int published = 0;
        int skipped = 0;
        for (Article article : candidates) {
            if (!requested.isEmpty() && !requested.contains(article.getId())) {
                skipped++;
                continue;
            }
            if (article.getStatus() != ArticleStatus.review) {
                log.warn("Skipping article id={} slug={} because status is {}", article.getId(), article.getSlug(), article.getStatus());
                skipped++;
                continue;
            }
            if (article.getContentMarkdown() == null || article.getContentMarkdown().isBlank()) {
                log.warn("Skipping article id={} slug={} because contentMarkdown is empty", article.getId(), article.getSlug());
                skipped++;
                continue;
            }
            article.publishNow();
            articles.save(article);
            published++;
            log.info("Published reviewed article id={} slug={}", article.getId(), article.getSlug());
        }
        return new PublishResult(requestedIds, confirmedAllReview, published, skipped);
    }

    record PublishResult(List<Long> requestedIds, boolean confirmedAllReview, int published, int skipped) {
    }
}
