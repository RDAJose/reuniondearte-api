package com.reuniondearte.api.importer.legacy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.category.Category;
import com.reuniondearte.api.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LegacyCandidateImporter {
    private static final String IMPORT_SOURCE = "legacy-scanner-json";
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final LegacyTextCleaner cleaner;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LegacyCandidateImporter(ArticleRepository articleRepository, CategoryRepository categoryRepository, LegacyTextCleaner cleaner) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.cleaner = cleaner;
    }

    @Transactional
    public LegacyCandidateImportReport importCandidates(Path candidatesFile, String backupPath) throws IOException {
        List<LegacyArticleCandidate> candidates = objectMapper.readValue(
                candidatesFile.toFile(),
                new TypeReference<>() {
                }
        );

        List<LegacyCandidateImportReport.ImportedArticle> articles = new ArrayList<>();
        List<LegacyCandidateImportReport.SkippedCandidate> skipped = new ArrayList<>();
        Map<String, Integer> categoriesUsed = new LinkedHashMap<>();
        int imported = 0;
        int updated = 0;
        int failed = 0;
        int withWarnings = 0;
        int referencedImages = 0;

        for (LegacyArticleCandidate candidate : candidates) {
            List<String> warnings = new ArrayList<>(candidate.warnings() == null ? List.of() : candidate.warnings());
            referencedImages += candidate.images() == null ? 0 : candidate.images().size();
            try {
                String slug = requireText(candidate.slug(), "missing-slug");
                String title = requireText(candidate.title(), "missing-title");
                String originalPath = requireText(candidate.originalPath(), "missing-original-path");
                Category category = resolveCategory(candidate.category(), warnings);
                String categorySlug = category.getSlug();
                String excerpt = excerpt(candidate);
                String contentMarkdown = cleaner.normalize(candidate.text());
                Integer readingTime = readingTime(candidate.wordCount());

                var existing = articleRepository.findBySlug(slug);
                if (existing.isPresent()) {
                    Article article = existing.get();
                    if (!sameText(article.getImportOriginalPath(), originalPath)) {
                        skipped.add(new LegacyCandidateImportReport.SkippedCandidate(slug, title, originalPath, "duplicate-slug-different-source"));
                        continue;
                    }
                    article.applyLegacyDraftImport(title, slug, excerpt, contentMarkdown, category, IMPORT_SOURCE, originalPath, readingTime);
                    articleRepository.save(article);
                    updated++;
                } else {
                    Article article = new Article();
                    article.applyLegacyDraftImport(title, slug, excerpt, contentMarkdown, category, IMPORT_SOURCE, originalPath, readingTime);
                    articleRepository.save(article);
                    imported++;
                }

                categoriesUsed.merge(categorySlug, 1, Integer::sum);
                if (!warnings.isEmpty()) {
                    withWarnings++;
                }
                articles.add(new LegacyCandidateImportReport.ImportedArticle(
                        slug,
                        title,
                        "draft",
                        categorySlug,
                        originalPath,
                        warnings,
                        candidate.images() == null ? 0 : candidate.images().size()
                ));
            } catch (RuntimeException exception) {
                failed++;
                skipped.add(new LegacyCandidateImportReport.SkippedCandidate(
                        candidate.slug(),
                        candidate.title(),
                        candidate.originalPath(),
                        "failed: " + exception.getMessage()
                ));
            }
        }

        int skippedDuplicates = (int) skipped.stream()
                .filter(item -> item.reason().startsWith("duplicate-slug"))
                .count();

        return new LegacyCandidateImportReport(
                java.time.Instant.now().toString(),
                candidatesFile.toAbsolutePath().normalize().toString(),
                backupPath,
                candidates.size(),
                imported,
                updated,
                skippedDuplicates,
                failed,
                withWarnings,
                referencedImages,
                categoriesUsed,
                articles,
                skipped
        );
    }

    private Category resolveCategory(String candidateCategory, List<String> warnings) {
        String slug = categorySlug(candidateCategory);
        if (slug.isBlank()) {
            warnings.add("missing-category-used-cultura");
            slug = "cultura";
        }
        return categoryRepository.findBySlug(slug)
                .or(() -> categoryRepository.findBySlug("cultura"))
                .orElseThrow(() -> new IllegalStateException("Missing fallback category cultura"));
    }

    private String categorySlug(String value) {
        String normalized = slugify(value);
        return switch (normalized) {
            case "cine" -> "cine";
            case "musica", "msica", "m-sica" -> "musica";
            case "arte" -> "arte";
            case "libros" -> "libros";
            case "relatos", "info-jose", "trabajos-jose", "audio" -> "cultura";
            default -> normalized;
        };
    }

    private String excerpt(LegacyArticleCandidate candidate) {
        String excerpt = cleaner.normalize(candidate.excerpt());
        if (!excerpt.isBlank()) {
            return excerpt;
        }
        return cleaner.excerpt(candidate.text());
    }

    private Integer readingTime(int wordCount) {
        if (wordCount <= 0) {
            return null;
        }
        return Math.max(1, (int) Math.ceil(wordCount / 220.0));
    }

    private String requireText(String value, String message) {
        String normalized = cleaner.normalize(value);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private boolean sameText(String left, String right) {
        return cleaner.normalize(left).equals(cleaner.normalize(right));
    }

    private String slugify(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String cleaned = value
                .replace("Ãº", "u")
                .replace("Ãš", "u")
                .replace("ú", "u")
                .replace("Ú", "u")
                .toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(cleaned, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
