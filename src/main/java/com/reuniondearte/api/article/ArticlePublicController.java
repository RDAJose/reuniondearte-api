package com.reuniondearte.api.article;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArticlePublicController {
    private final ArticleRepository articles;

    public ArticlePublicController(ArticleRepository articles) {
        this.articles = articles;
    }

    @GetMapping("/articles")
    public List<ArticleSummaryResponse> listPublishedArticles() {
        return articles.findByStatusOrderByPublishedAtDesc(ArticleStatus.published)
                .stream()
                .map(ArticleSummaryResponse::from)
                .toList();
    }

    @GetMapping("/articles/{slug}")
    public ResponseEntity<ArticleDetailResponse> getPublishedArticle(@PathVariable String slug) {
        return articles.findBySlugAndStatus(slug, ArticleStatus.published)
                .map(ArticleDetailResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

