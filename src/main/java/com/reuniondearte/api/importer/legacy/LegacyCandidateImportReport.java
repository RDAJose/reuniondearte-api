package com.reuniondearte.api.importer.legacy;

import java.util.List;
import java.util.Map;

public record LegacyCandidateImportReport(
        String importedAt,
        String candidatesFile,
        String backupPath,
        int candidatesRead,
        int imported,
        int updated,
        int skippedDuplicates,
        int failed,
        int withWarnings,
        int referencedImages,
        Map<String, Integer> categoriesUsed,
        List<ImportedArticle> articles,
        List<SkippedCandidate> skipped
) {
    public record ImportedArticle(
            String slug,
            String title,
            String status,
            String category,
            String originalPath,
            List<String> warnings,
            int referencedImages
    ) {
    }

    public record SkippedCandidate(
            String slug,
            String title,
            String originalPath,
            String reason
    ) {
    }
}
