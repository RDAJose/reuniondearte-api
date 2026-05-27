package com.reuniondearte.api.importer.legacy;

import java.util.List;

public record LegacyArticleCandidate(
        String title,
        String slug,
        String date,
        String category,
        String excerpt,
        String text,
        int wordCount,
        List<LegacyLink> links,
        List<LegacyImage> images,
        String originalPath,
        List<String> warnings
) {
    public record LegacyLink(String text, String href) {
    }

    public record LegacyImage(String src, String alt, String caption) {
    }
}
