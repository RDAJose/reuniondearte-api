package com.reuniondearte.api.importer.legacy;

import java.util.List;

record ImportedArticleNormalizeReport(
        String createdAt,
        boolean apply,
        String action,
        int total,
        int draft,
        int review,
        int published,
        int readyMarkdown,
        int needsHtmlConversion,
        int withR2Images,
        int withExternalImages,
        int withoutImage,
        int r2LegalReviewPending,
        int legalReviewPending,
        int manualReview,
        int converted,
        int imagesImported,
        int metadataUpdated,
        List<ArticleEntry> articles
) {
    record ArticleEntry(
            Long id,
            String slug,
            String title,
            String status,
            List<String> classifications,
            List<String> r2Images,
            List<String> externalImages,
            List<String> notes
    ) {
    }
}
