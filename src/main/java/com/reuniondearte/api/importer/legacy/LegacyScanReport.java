package com.reuniondearte.api.importer.legacy;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record LegacyScanReport(
        Instant scannedAt,
        String legacyRoot,
        int htmlAnalyzed,
        int candidatesDetected,
        int candidatesWithImages,
        int candidatesWithPossibleMojibake,
        Map<String, Integer> discardedByReason,
        List<LegacyArticleCandidate> candidates,
        List<LegacyDiscardedPage> discarded
) {
    public record LegacyDiscardedPage(String originalPath, String reason, String detail) {
    }
}
