package com.reuniondearte.api.category;

import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import com.reuniondearte.api.article.ArticleSummaryResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryPublicController {
    private final CategoryRepository categories;
    private final ArticleRepository articles;

    public CategoryPublicController(CategoryRepository categories, ArticleRepository articles) {
        this.categories = categories;
        this.articles = articles;
    }

    @GetMapping
    public List<CategoryResponse> listCategories() {
        return categories.findAllByOrderBySortOrderAscNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{slug}/articles")
    public List<ArticleSummaryResponse> listPublishedArticlesByCategory(@PathVariable String slug) {
        return articles.findByPrimaryCategorySlugAndStatusOrderByPublishedAtDesc(slug, ArticleStatus.published)
                .stream()
                .map(ArticleSummaryResponse::from)
                .toList();
    }
}

