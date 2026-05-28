package com.reuniondearte.api.importer.legacy;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ImportedArticleNormalizeProperties(
        boolean normalizeImportedArticles,
        String articleNormalizeAction,
        boolean articleNormalizeApply,
        boolean articleNormalizeClearHtml,
        Long articleNormalizeArticleId,
        String articleNormalizeImageRole,
        String articleNormalizeSourceUrl,
        String articleNormalizeCredit,
        String articleNormalizeRightsNotes,
        String articleNormalizeCaption,
        String articleNormalizeAltText,
        boolean publishReviewedArticles,
        boolean publishReviewedConfirmed,
        List<Long> reviewedArticleIds
) {
    public String action() {
        return articleNormalizeAction == null || articleNormalizeAction.isBlank()
                ? "report"
                : articleNormalizeAction.trim().toLowerCase();
    }
}
