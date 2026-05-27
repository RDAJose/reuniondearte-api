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
}

