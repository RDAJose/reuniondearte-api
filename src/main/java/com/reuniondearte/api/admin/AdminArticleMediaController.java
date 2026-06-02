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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/articles")
public class AdminArticleMediaController {
    private static final String FEATURED_ROLE = "ARTICLE_FEATURED";
    private static final List<String> FEATURED_ROLES = List.of("ARTICLE_FEATURED", "featured", "cover");
    private static final List<String> MEDIA_FILE_ROLES = List.of("audio", "video");

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
        replaceFeaturedAssociation(article, savedMediaAsset);
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
        replaceFeaturedAssociation(article, savedMediaAsset);
        article.updateCoverMedia(savedMediaAsset);
        Article savedArticle = articles.save(article);
        return AdminArticleCoverResponse.from(savedArticle, savedMediaAsset);
    }

    @DeleteMapping("/{id}/cover")
    @Transactional
    public ResponseEntity<Void> removeCover(@PathVariable Long id) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        articleMedia.deleteByArticleIdAndRoleIn(article.getId(), FEATURED_ROLES);
        article.updateCoverMedia(null);
        articles.save(article);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/body-images")
    public List<AdminArticleMediaResponse> listBodyImages(@PathVariable Long id) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        return articleMedia.findByArticleIdAndRoleOrderByCreatedAtAscIdAsc(article.getId(), "body").stream()
                .map(AdminArticleMediaResponse::from)
                .toList();
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

    @DeleteMapping("/{id}/body-images/{articleMediaId}")
    @Transactional
    public ResponseEntity<Void> removeBodyImage(
            @PathVariable Long id,
            @PathVariable Long articleMediaId
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        ArticleMedia association = articleMedia.findByIdAndArticleIdAndRole(articleMediaId, article.getId(), "body")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Body image not found"));
        articleMedia.delete(association);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/media-files")
    public List<AdminArticleMediaFileResponse> listMediaFiles(@PathVariable Long id) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        return articleMedia.findByArticleIdAndRoleInOrderByCreatedAtAscIdAsc(article.getId(), MEDIA_FILE_ROLES).stream()
                .map(AdminArticleMediaFileResponse::from)
                .toList();
    }

    @PostMapping(value = "/{id}/media-files/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public AdminArticleMediaFileResponse uploadAudioFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String credit,
            @RequestParam(required = false) String sourceUrl,
            @RequestParam(required = false) String rightsNotes
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        int sortOrder = articleMedia.countByArticleIdAndRole(article.getId(), "audio");
        MediaStorageService.StoredFile storedFile = mediaStorage.storeArticleAudio(article.getSlug(), mediaFilenameBase("audio", sortOrder), file);
        MediaAsset savedMediaAsset = saveMediaFileAsset(storedFile, "audio", title, caption, credit, sourceUrl, rightsNotes);
        ArticleMedia savedArticleMedia = articleMedia.save(ArticleMedia.create(article, savedMediaAsset, "audio", sortOrder));
        return AdminArticleMediaFileResponse.from(savedArticleMedia);
    }

    @PostMapping(value = "/{id}/media-files/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public AdminArticleMediaFileResponse uploadVideoFile(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String credit,
            @RequestParam(required = false) String sourceUrl,
            @RequestParam(required = false) String rightsNotes
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        int sortOrder = articleMedia.countByArticleIdAndRole(article.getId(), "video");
        MediaStorageService.StoredFile storedFile = mediaStorage.storeArticleVideo(article.getSlug(), mediaFilenameBase("video", sortOrder), file);
        MediaAsset savedMediaAsset = saveMediaFileAsset(storedFile, "video", title, caption, credit, sourceUrl, rightsNotes);
        ArticleMedia savedArticleMedia = articleMedia.save(ArticleMedia.create(article, savedMediaAsset, "video", sortOrder));
        return AdminArticleMediaFileResponse.from(savedArticleMedia);
    }

    @DeleteMapping("/{id}/media-files/{articleMediaId}")
    @Transactional
    public ResponseEntity<Void> removeMediaFile(
            @PathVariable Long id,
            @PathVariable Long articleMediaId
    ) {
        Article article = articles.findWithRelationsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        ArticleMedia association = articleMedia.findByIdAndArticleIdAndRoleIn(articleMediaId, article.getId(), MEDIA_FILE_ROLES)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media file not found"));
        articleMedia.delete(association);
        return ResponseEntity.noContent().build();
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

    private MediaAsset saveMediaFileAsset(
            MediaStorageService.StoredFile storedFile,
            String mediaType,
            String title,
            String caption,
            String credit,
            String sourceUrl,
            String rightsNotes
    ) {
        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.applyStoredMediaFile(
                mediaType,
                storedFile.storageProvider(),
                storedFile.storagePath(),
                storedFile.publicUrl(),
                storedFile.filename(),
                storedFile.mimeType(),
                storedFile.sizeBytes(),
                blankToNull(title),
                blankToNull(caption),
                blankToNull(credit),
                blankToNull(sourceUrl),
                blankToNull(rightsNotes)
        );
        return mediaAssets.save(mediaAsset);
    }

    private String mediaFilenameBase(String kind, int sortOrder) {
        return kind + "-" + (sortOrder + 1) + "-" + System.currentTimeMillis();
    }

    private void replaceFeaturedAssociation(Article article, MediaAsset mediaAsset) {
        articleMedia.deleteByArticleIdAndRoleIn(article.getId(), FEATURED_ROLES);
        articleMedia.save(ArticleMedia.create(article, mediaAsset, FEATURED_ROLE, 0));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
