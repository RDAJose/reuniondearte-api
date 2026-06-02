package com.reuniondearte.api.admin;

import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.MediaAsset;
import java.time.OffsetDateTime;

public record AdminArticleMediaFileResponse(
        Long id,
        Long mediaAssetId,
        String kind,
        String publicUrl,
        String title,
        String caption,
        String credit,
        String sourceUrl,
        String rightsNotes,
        Boolean active,
        OffsetDateTime createdAt,
        String markdownSnippet
) {
    static AdminArticleMediaFileResponse from(ArticleMedia articleMedia) {
        MediaAsset mediaAsset = articleMedia.getMediaAsset();
        String kind = mediaAsset.getMediaType();
        String title = blankToNull(mediaAsset.getTitle());
        return new AdminArticleMediaFileResponse(
                articleMedia.getId(),
                mediaAsset.getId(),
                kind,
                mediaAsset.getPublicUrl(),
                title,
                mediaAsset.getCaption(),
                mediaAsset.getCredit(),
                mediaAsset.getSourceUrl(),
                mediaAsset.getRightsNotes(),
                true,
                articleMedia.getCreatedAt(),
                markdownSnippet(kind, title, mediaAsset.getPublicUrl())
        );
    }

    private static String markdownSnippet(String kind, String title, String publicUrl) {
        String fallbackTitle = "audio".equals(kind) ? "Audio" : "V\u00eddeo";
        String label = title == null ? fallbackTitle : title;
        return "[" + kind + ": " + label + "](" + publicUrl + ")";
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
