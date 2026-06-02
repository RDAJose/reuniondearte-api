package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.MediaAsset;
import java.time.OffsetDateTime;

public record AdminArticleMediaResponse(
        Long id,
        Long mediaAssetId,
        String publicUrl,
        String altText,
        String caption,
        String credit,
        String sourceUrl,
        String rightsNotes,
        @JsonProperty("storage_path") String storagePath,
        String filename,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("size_bytes") Long sizeBytes,
        Integer width,
        Integer height,
        Boolean active,
        OffsetDateTime createdAt,
        String markdownSnippet
) {
    static AdminArticleMediaResponse from(MediaAsset mediaAsset) {
        return new AdminArticleMediaResponse(
                null,
                mediaAsset.getId(),
                mediaAsset.getPublicUrl(),
                mediaAsset.getAltText(),
                mediaAsset.getCaption(),
                mediaAsset.getCredit(),
                mediaAsset.getSourceUrl(),
                mediaAsset.getRightsNotes(),
                mediaAsset.getStoragePath(),
                mediaAsset.getFilename(),
                mediaAsset.getMimeType(),
                mediaAsset.getSizeBytes(),
                mediaAsset.getWidth(),
                mediaAsset.getHeight(),
                true,
                null,
                markdownSnippet(mediaAsset)
        );
    }

    static AdminArticleMediaResponse from(ArticleMedia articleMedia) {
        MediaAsset mediaAsset = articleMedia.getMediaAsset();
        return new AdminArticleMediaResponse(
                articleMedia.getId(),
                mediaAsset.getId(),
                mediaAsset.getPublicUrl(),
                mediaAsset.getAltText(),
                mediaAsset.getCaption(),
                mediaAsset.getCredit(),
                mediaAsset.getSourceUrl(),
                mediaAsset.getRightsNotes(),
                mediaAsset.getStoragePath(),
                mediaAsset.getFilename(),
                mediaAsset.getMimeType(),
                mediaAsset.getSizeBytes(),
                mediaAsset.getWidth(),
                mediaAsset.getHeight(),
                true,
                articleMedia.getCreatedAt(),
                markdownSnippet(mediaAsset)
        );
    }

    private static String markdownSnippet(MediaAsset mediaAsset) {
        String alt = mediaAsset.getAltText() == null ? "" : mediaAsset.getAltText().trim();
        StringBuilder snippet = new StringBuilder("![" + alt + "](" + mediaAsset.getPublicUrl() + ")");
        String caption = cleanTrailingPunctuation(mediaAsset.getCaption());
        String credit = cleanTrailingPunctuation(mediaAsset.getCredit());
        if (!caption.isBlank() || !credit.isBlank()) {
            snippet.append("\n\n*");
            if (!caption.isBlank()) {
                snippet.append(caption);
            }
            if (!credit.isBlank()) {
                if (!caption.isBlank()) {
                    snippet.append(". ");
                }
                snippet.append("Cr\u00e9dito: ").append(credit);
            }
            snippet.append(".*");
        }
        return snippet.toString();
    }

    private static String cleanTrailingPunctuation(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim().replaceAll("[.]+$", "");
    }
}
