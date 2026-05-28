package com.reuniondearte.api.admin;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.ArticleMediaRepository;
import com.reuniondearte.api.media.MediaAsset;
import com.reuniondearte.api.media.MediaAssetRepository;
import com.reuniondearte.api.media.MediaStorageService;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final ArticleMediaRepository articleMedia;
    private final MediaStorageService mediaStorage;

    public AdminArticleMediaController(
            ArticleRepository articles,
            MediaAssetRepository mediaAssets,
            ArticleMediaRepository articleMedia,
            MediaStorageService mediaStorage
    ) {
        this.articles = articles;
        this.mediaAssets = mediaAssets;
        this.articleMedia = articleMedia;
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

    @PatchMapping("/{id}/cover/metadata")
    @Transactional
    public AdminArticleCoverResponse updateCoverMetadata(
            @PathVariable Long id,
            @Valid @RequestBody AdminImageMetadataRequest request
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        MediaAsset coverMedia = article.getCoverMedia();
        if (coverMedia == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article has no cover image");
        }

        coverMedia.updateEditorialMetadata(
                request.altText().trim(),
                blankToNull(request.caption()),
                blankToNull(request.credit()),
                blankToNull(request.sourceUrl()),
                blankToNull(request.rightsNotes())
        );
        MediaAsset savedMediaAsset = mediaAssets.save(coverMedia);
        return AdminArticleCoverResponse.from(article, savedMediaAsset);
    }

    @PostMapping("/{id}/cover/import")
    @Transactional
    public AdminArticleCoverResponse importCover(
            @PathVariable Long id,
            @Valid @RequestBody AdminImageImportRequest request
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        MediaStorageService.StoredImage storedImage = mediaStorage.importArticleImage(article.getSlug(), "cover", request.imageUrl());
        MediaAsset savedMediaAsset = saveMediaAsset(storedImage, request);
        article.updateCoverMedia(savedMediaAsset);
        Article savedArticle = articles.save(article);
        return AdminArticleCoverResponse.from(savedArticle, savedMediaAsset);
    }

    @PostMapping(value = "/{id}/body-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public AdminArticleMediaResponse uploadBodyImage(
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
        int sortOrder = articleMedia.countByArticleIdAndRole(article.getId(), "body");
        String filenameBase = "body-" + (sortOrder + 1);
        MediaStorageService.StoredImage storedImage = mediaStorage.storeArticleImage(article.getSlug(), filenameBase, file);
        MediaAsset savedMediaAsset = saveMediaAsset(storedImage, altText, caption, credit, sourceUrl, rightsNotes);
        ArticleMedia savedArticleMedia = articleMedia.save(ArticleMedia.create(article, savedMediaAsset, "body", sortOrder));
        return AdminArticleMediaResponse.from(savedArticleMedia);
    }

    @PostMapping("/{id}/body-images/import")
    @Transactional
    public AdminArticleMediaResponse importBodyImage(
            @PathVariable Long id,
            @Valid @RequestBody AdminImageImportRequest request
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        int sortOrder = articleMedia.countByArticleIdAndRole(article.getId(), "body");
        String filenameBase = "body-" + (sortOrder + 1);
        MediaStorageService.StoredImage storedImage = mediaStorage.importArticleImage(article.getSlug(), filenameBase, request.imageUrl());
        MediaAsset savedMediaAsset = saveMediaAsset(storedImage, request);
        ArticleMedia savedArticleMedia = articleMedia.save(ArticleMedia.create(article, savedMediaAsset, "body", sortOrder));
        return AdminArticleMediaResponse.from(savedArticleMedia);
    }

    private MediaAsset saveMediaAsset(MediaStorageService.StoredImage storedImage, AdminImageImportRequest request) {
        return saveMediaAsset(
                storedImage,
                request.altText(),
                request.caption(),
                request.credit(),
                request.sourceUrl(),
                request.rightsNotes()
        );
    }

    private MediaAsset saveMediaAsset(
            MediaStorageService.StoredImage storedImage,
            String altText,
            String caption,
            String credit,
            String sourceUrl,
            String rightsNotes
    ) {
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
        return mediaAssets.save(mediaAsset);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
