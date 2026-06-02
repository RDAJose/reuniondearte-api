package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import com.reuniondearte.api.category.Category;
import com.reuniondearte.api.category.CategoryRepository;
import com.reuniondearte.api.media.ArticleMediaRepository;
import com.reuniondearte.api.seo.SeoMetadata;
import com.reuniondearte.api.seo.SeoMetadataRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleController {
    private final ArticleRepository articles;
    private final CategoryRepository categories;
    private final SeoMetadataRepository seoMetadata;
    private final ArticleMediaRepository articleMedia;

    public AdminArticleController(
            ArticleRepository articles,
            CategoryRepository categories,
            SeoMetadataRepository seoMetadata,
            ArticleMediaRepository articleMedia
    ) {
        this.articles = articles;
        this.categories = categories;
        this.seoMetadata = seoMetadata;
        this.articleMedia = articleMedia;
    }

    @GetMapping
    public List<AdminArticleResponse> list(@RequestParam(defaultValue = "draft") ArticleStatus status) {
        return articles.findByStatusOrderByUpdatedAtDesc(status).stream()
                .map(article -> AdminArticleResponse.from(article, seoMetadata.findByArticleId(article.getId()).orElse(null)))
                .toList();
    }

    @GetMapping("/{id}")
    public AdminArticleResponse detail(@PathVariable Long id) {
        Article article = articleOr404(id);
        return AdminArticleResponse.from(
                article,
                seoMetadata.findByArticleId(article.getId()).orElse(null),
                articleMedia.findByArticleIdAndRoleOrderByCreatedAtAscIdAsc(article.getId(), "body")
                        .stream()
                        .map(AdminArticleMediaResponse::from)
                        .toList()
        );
    }

    @PostMapping
    @Transactional
    public ResponseEntity<AdminArticleResponse> create(@Valid @RequestBody AdminArticleRequest request) {
        articles.findBySlug(request.slug()).ifPresent(article -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Article slug already exists");
        });
        Article article = new Article();
        apply(article, request, request.status() == null ? ArticleStatus.draft : request.status());
        Article saved = articles.save(article);
        SeoMetadata seo = saveSeo(saved, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminArticleResponse.from(saved, seo));
    }

    @PutMapping("/{id}")
    @Transactional
    public AdminArticleResponse update(@PathVariable Long id, @Valid @RequestBody AdminArticleRequest request) {
        Article article = articleOr404(id);
        articles.findBySlug(request.slug())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Article slug already exists");
                });
        apply(article, request, request.status() == null ? article.getStatus() : request.status());
        Article saved = articles.save(article);
        SeoMetadata seo = saveSeo(saved, request);
        return AdminArticleResponse.from(saved, seo);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public AdminArticleResponse changeStatus(@PathVariable Long id, @Valid @RequestBody AdminArticleStatusRequest request) {
        Article article = articleOr404(id);
        article.changeStatus(request.status());
        Article saved = articles.save(article);
        return AdminArticleResponse.from(saved, seoMetadata.findByArticleId(saved.getId()).orElse(null));
    }

    @PatchMapping("/{id}/publish")
    @Transactional
    public AdminArticleResponse publish(@PathVariable Long id) {
        Article article = articleOr404(id);
        article.publishNow();
        Article saved = articles.save(article);
        return AdminArticleResponse.from(saved, seoMetadata.findByArticleId(saved.getId()).orElse(null));
    }

    @PatchMapping("/{id}/draft")
    @Transactional
    public AdminArticleResponse draft(@PathVariable Long id) {
        Article article = articleOr404(id);
        article.moveToDraft();
        Article saved = articles.save(article);
        return AdminArticleResponse.from(saved, seoMetadata.findByArticleId(saved.getId()).orElse(null));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Article article = articleOr404(id);
        if (article.getStatus() == ArticleStatus.published) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Move the article to draft before deleting it");
        }
        articles.delete(article);
        return ResponseEntity.noContent().build();
    }

    private void apply(Article article, AdminArticleRequest request, ArticleStatus status) {
        article.applyEditorialUpdate(
                request.title(),
                request.slug(),
                blankToNull(request.excerpt()),
                blankToNull(request.contentMarkdown()),
                status,
                category(request.category()),
                publishedAt(status, request.publishedAt()),
                blankToNull(request.canonicalUrl())
        );
    }

    private SeoMetadata saveSeo(Article article, AdminArticleRequest request) {
        SeoMetadata seo = seoMetadata.findByArticleId(article.getId()).orElseGet(SeoMetadata::new);
        seo.applyEditorialUpdate(
                article,
                blankToNull(request.metaTitle()),
                blankToNull(request.metaDescription()),
                blankToNull(request.canonicalUrl()),
                request.noindex()
        );
        return seoMetadata.save(seo);
    }

    private Article articleOr404(Long id) {
        return articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    private Category category(String slug) {
        String categorySlug = blankToNull(slug) == null ? "cultura" : slug.trim().toLowerCase();
        return categories.findBySlug(categorySlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown category: " + categorySlug));
    }

    private OffsetDateTime publishedAt(ArticleStatus status, OffsetDateTime publishedAt) {
        if (status != ArticleStatus.published) {
            return null;
        }
        return publishedAt == null ? OffsetDateTime.now() : publishedAt;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
