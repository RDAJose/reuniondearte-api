package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.media.MediaAsset;
import com.reuniondearte.api.media.MediaAssetRepository;
import com.reuniondearte.api.media.MediaStorageService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleMediaController {
    private final ArticleRepository articles;
    private final MediaAssetRepository mediaAssets;
    private final MediaStorageService mediaStorage;

    public AdminArticleMediaController(
            ArticleRepository articles,
            MediaAssetRepository mediaAssets,
            MediaStorageService mediaStorage
    ) {
        this.articles = articles;
        this.mediaAssets = mediaAssets;
        this.mediaStorage = mediaStorage;
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public AdminArticleCoverResponse uploadCover(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam String altText,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String credit,
            @RequestParam(required = false) String sourceUrl,
            @RequestParam(required = false) String rightsNotes
    ) {
        if (altText == null || altText.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "altText is required");
        }
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        MediaStorageService.StoredImage storedImage = mediaStorage.storeArticleCover(article.getSlug(), file);
        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.applyStoredImage(
                storedImage.storageProvider(),
                storedImage.storagePath(),
                storedImage.publicUrl(),
                storedImage.filename(),
                storedImage.mimeType(),
                storedImage.sizeBytes(),
                storedImage.width(),
                storedImage.height(),
                altText.trim(),
                blankToNull(caption),
                blankToNull(credit),
                blankToNull(sourceUrl),
                blankToNull(rightsNotes)
        );
        MediaAsset savedMediaAsset = mediaAssets.save(mediaAsset);
        article.updateCoverMedia(savedMediaAsset);
        Article savedArticle = articles.save(article);
        return AdminArticleCoverResponse.from(savedArticle, savedMediaAsset);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
