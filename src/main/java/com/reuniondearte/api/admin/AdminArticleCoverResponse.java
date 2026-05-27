package com.reuniondearte.api.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.media.MediaAsset;

public record AdminArticleCoverResponse(
        Long articleId,
        Long mediaAssetId,
        @JsonProperty("coverImage") String coverImage,
        @JsonProperty("coverAlt") String coverAlt,
        @JsonProperty("coverCaption") String coverCaption,
        @JsonProperty("coverCredit") String coverCredit,
        @JsonProperty("storage_path") String storagePath,
        String filename,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("size_bytes") Long sizeBytes,
        Integer width,
        Integer height
) {
    static AdminArticleCoverResponse from(Article article, MediaAsset mediaAsset) {
        return new AdminArticleCoverResponse(
                article.getId(),
                mediaAsset.getId(),
                mediaAsset.getPublicUrl(),
                mediaAsset.getAltText(),
                mediaAsset.getCaption(),
                mediaAsset.getCredit(),
                mediaAsset.getStoragePath(),
                mediaAsset.getFilename(),
                mediaAsset.getMimeType(),
                mediaAsset.getSizeBytes(),
                mediaAsset.getWidth(),
                mediaAsset.getHeight()
        );
    }
}
