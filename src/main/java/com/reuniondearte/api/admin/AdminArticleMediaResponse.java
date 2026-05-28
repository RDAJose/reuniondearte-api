package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.MediaAsset;

public record AdminArticleMediaResponse(
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
        String markdownSnippet
) {
    static AdminArticleMediaResponse from(MediaAsset mediaAsset) {
        return new AdminArticleMediaResponse(
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
                markdownSnippet(mediaAsset)
        );
    }

    static AdminArticleMediaResponse from(ArticleMedia articleMedia) {
        return from(articleMedia.getMediaAsset());
    }

    private static String markdownSnippet(MediaAsset mediaAsset) {
        String alt = mediaAsset.getAltText() == null ? "" : mediaAsset.getAltText();
        String caption = mediaAsset.getCaption() == null ? "" : mediaAsset.getCaption();
        String credit = mediaAsset.getCredit() == null ? "" : mediaAsset.getCredit();
        return "![" + alt + "](" + mediaAsset.getPublicUrl() + ")\n\n*" + caption + ". Crédito: " + credit + ".*";
    }
}
