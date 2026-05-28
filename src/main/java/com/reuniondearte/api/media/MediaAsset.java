package com.reuniondearte.api.media;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "media_assets")
public class MediaAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String mediaType;

    @Column(nullable = false, length = 40)
    private String storageProvider;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String storagePath;

    @Column(columnDefinition = "TEXT")
    private String publicUrl;

    @Column(nullable = false, length = 255)
    private String filename;

    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;

    @Column(columnDefinition = "TEXT")
    private String altText;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(columnDefinition = "TEXT")
    private String credit;

    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(columnDefinition = "TEXT")
    private String rightsNotes;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public String getAltText() {
        return altText;
    }

    public String getCaption() {
        return caption;
    }

    public String getCredit() {
        return credit;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getRightsNotes() {
        return rightsNotes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public void applyLocalImage(
            String storagePath,
            String publicUrl,
            String filename,
            String mimeType,
            Long sizeBytes,
            Integer width,
            Integer height,
            String altText,
            String caption,
            String credit,
            String sourceUrl,
            String rightsNotes
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        this.mediaType = "image";
        this.storageProvider = "local";
        this.storagePath = storagePath;
        this.publicUrl = publicUrl;
        this.filename = filename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
        this.altText = altText;
        this.caption = caption;
        this.credit = credit;
        this.sourceUrl = sourceUrl;
        this.rightsNotes = rightsNotes;
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    public void applyStoredImage(
            String storageProvider,
            String storagePath,
            String publicUrl,
            String filename,
            String mimeType,
            Long sizeBytes,
            Integer width,
            Integer height,
            String altText,
            String caption,
            String credit,
            String sourceUrl,
            String rightsNotes
    ) {
        OffsetDateTime now = OffsetDateTime.now();
        this.mediaType = "image";
        this.storageProvider = storageProvider;
        this.storagePath = storagePath;
        this.publicUrl = publicUrl;
        this.filename = filename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
        this.altText = altText;
        this.caption = caption;
        this.credit = credit;
        this.sourceUrl = sourceUrl;
        this.rightsNotes = rightsNotes;
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    public void updateEditorialMetadata(String altText, String caption, String credit, String sourceUrl, String rightsNotes) {
        this.altText = altText;
        this.caption = caption;
        this.credit = credit;
        this.sourceUrl = sourceUrl;
        this.rightsNotes = rightsNotes;
        this.updatedAt = OffsetDateTime.now();
    }
}
