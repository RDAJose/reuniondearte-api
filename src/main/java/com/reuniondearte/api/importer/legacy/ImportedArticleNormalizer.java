package com.reuniondearte.api.importer.legacy;

import com.reuniondearte.api.article.Article;
import com.reuniondearte.api.article.ArticleRepository;
import com.reuniondearte.api.article.ArticleStatus;
import com.reuniondearte.api.config.StorageProperties;
import com.reuniondearte.api.media.ArticleMedia;
import com.reuniondearte.api.media.ArticleMediaRepository;
import com.reuniondearte.api.media.MediaAsset;
import com.reuniondearte.api.media.MediaAssetRepository;
import com.reuniondearte.api.media.MediaStorageService;
import com.reuniondearte.api.revision.ArticleRevision;
import com.reuniondearte.api.revision.ArticleRevisionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class ImportedArticleNormalizer {
    private static final Pattern MARKDOWN_IMAGE = Pattern.compile("!\\[([^]]*)]\\((https?://[^)\\s]+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_LIKE = Pattern.compile("<\\s*/?\\s*(p|div|h[1-6]|ul|ol|li|blockquote|img|a|strong|em|br)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern MARKDOWN_SIGNAL = Pattern.compile("(^|\\n)\\s*(#{1,6}\\s|[-*]\\s|\\d+\\.\\s|>\\s)|\\[[^]]+]\\([^)]+\\)|!\\[[^]]*]\\([^)]+\\)|\\*\\*[^*]+\\*\\*", Pattern.MULTILINE);
    private static final List<String> LEGACY_IMAGE_COLUMNS = List.of(
            "cover_image", "cover_image_url", "featured_image_url", "image_url", "legacy_image_url", "thumbnail_url"
    );

    private final ArticleRepository articles;
    private final MediaAssetRepository mediaAssets;
    private final ArticleMediaRepository articleMedia;
    private final ArticleRevisionRepository revisions;
    private final MediaStorageService mediaStorage;
    private final StorageProperties storageProperties;
    private final ImportedArticleNormalizeProperties properties;
    private final EntityManager entityManager;
    private final ImportedArticleHtmlToMarkdownConverter htmlToMarkdown = new ImportedArticleHtmlToMarkdownConverter();

    ImportedArticleNormalizer(
            ArticleRepository articles,
            MediaAssetRepository mediaAssets,
            ArticleMediaRepository articleMedia,
            ArticleRevisionRepository revisions,
            MediaStorageService mediaStorage,
            StorageProperties storageProperties,
            ImportedArticleNormalizeProperties properties,
            EntityManager entityManager
    ) {
        this.articles = articles;
        this.mediaAssets = mediaAssets;
        this.articleMedia = articleMedia;
        this.revisions = revisions;
        this.mediaStorage = mediaStorage;
        this.storageProperties = storageProperties;
        this.properties = properties;
        this.entityManager = entityManager;
    }

    @Transactional
    public ImportedArticleNormalizeReport run(String action, boolean apply, boolean clearHtml) {
        List<Article> allArticles = articles.findAll();
        Set<String> ownMediaPublicUrls = ownMediaPublicUrls();
        Map<Long, Map<String, String>> legacyImages = legacyImageColumns(allArticles);
        List<ImportedArticleNormalizeReport.ArticleEntry> entries = new ArrayList<>();
        int converted = 0;
        int imported = 0;
        int metadataUpdated = 0;

        for (Article article : allArticles) {
            Analysis analysis = analyze(article, legacyImages.getOrDefault(article.getId(), Map.of()), ownMediaPublicUrls);
            if ("convert".equals(action) && apply && analysis.needsHtmlConversion()) {
                revisions.save(ArticleRevision.importedArticleBackup(article, "Backup before imported article Markdown normalization"));
                article.normalizeImportedContent(htmlToMarkdown.convert(article.getContentHtml()), clearHtml);
                articles.save(article);
                converted++;
                analysis.notes().add("HTML converted to Markdown");
            }
            if ("images".equals(action)) {
                if (apply && !"s3".equalsIgnoreCase(storageProperties.storageProvider())) {
                    throw new IllegalStateException("Image import apply requires R2/S3 storage: set RDA_STORAGE_PROVIDER=s3");
                }
                if (apply) {
                    ImageImportResult result = importExternalImages(article, analysis.externalImages(), clearHtml);
                    imported += result.imported();
                    if (result.changed()) {
                        revisions.save(ArticleRevision.importedArticleBackup(article, "Backup before imported article image URL replacement"));
                        article.normalizeImportedContent(result.markdown(), clearHtml);
                        articles.save(article);
                    }
                    analysis.notes().addAll(result.notes());
                } else if (!analysis.externalImages().isEmpty()) {
                    analysis.notes().add("dry-run: external images would be downloaded, uploaded to R2, and replaced");
                }
            }
            if ("metadata".equals(action)
                    && propertiesMatch(article)
                    && applyExistingImageMetadata(article, apply)) {
                if (apply) {
                    metadataUpdated++;
                }
                analysis.notes().add(apply ? "existing image metadata updated" : "dry-run: existing image metadata would be updated");
            }
            entries.add(entry(article, analysis));
        }

        return report(action, apply, allArticles, entries, converted, imported, metadataUpdated);
    }

    private Analysis analyze(Article article, Map<String, String> legacyImageFields, Set<String> ownMediaPublicUrls) {
        List<String> classes = new ArrayList<>();
        List<String> notes = new ArrayList<>();
        String markdown = article.getContentMarkdown();
        String html = article.getContentHtml();
        boolean markdownBlank = isBlank(markdown);
        boolean htmlPresent = !isBlank(html);
        boolean markdownLooksHtml = !markdownBlank && HTML_LIKE.matcher(markdown).find();
        boolean markdownLooksPlain = !markdownBlank && !markdownLooksHtml && !MARKDOWN_SIGNAL.matcher(markdown).find();
        boolean needsHtmlConversion = markdownBlank && htmlPresent || markdownLooksHtml;

        if (!markdownBlank && !markdownLooksHtml && !markdownLooksPlain) {
            classes.add("listo para Markdown");
        }
        if (needsHtmlConversion) {
            classes.add("necesita conversion HTML -> Markdown");
        }
        if (markdownBlank) {
            notes.add("contentMarkdown empty");
        }
        if (markdownLooksHtml) {
            notes.add("contentMarkdown appears to contain HTML");
        }
        if (markdownLooksPlain) {
            notes.add("contentMarkdown may be plain text");
            classes.add("necesita revision manual");
        }

        List<ImportedArticleImageReference> r2Images = new ArrayList<>();
        List<ImportedArticleImageReference> externalImages = categorizedImages(article, legacyImageFields, ownMediaPublicUrls, r2Images);
        if (r2Images.isEmpty() && externalImages.isEmpty() && article.getCoverMedia() == null) {
            classes.add("no tiene imagen");
        }
        if (!r2Images.isEmpty()) {
            classes.add("imagen ya en R2");
        }
        if (!externalImages.isEmpty()) {
            classes.add("imagen externa recuperable");
        }
        if (r2LegalReviewPending(article, ownMediaPublicUrls)) {
            classes.add("imagen en R2 con revision legal pendiente");
        }
        if (externalLegalReviewPending(externalImages)) {
            classes.add("imagen con metadatos legales incompletos");
        }
        if (needsHtmlConversion && isBlank(html) && markdownLooksHtml) {
            classes.add("necesita revision manual");
        }
        return new Analysis(classes, notes, r2Images, externalImages, needsHtmlConversion);
    }

    private List<ImportedArticleImageReference> categorizedImages(
            Article article,
            Map<String, String> legacyImageFields,
            Set<String> ownMediaPublicUrls,
            List<ImportedArticleImageReference> r2Images
    ) {
        Map<String, ImportedArticleImageReference> externalImages = new LinkedHashMap<>();
        Map<String, ImportedArticleImageReference> r2ImageMap = new LinkedHashMap<>();
        collectMarkdownImages(article.getContentMarkdown(), "contentMarkdown", externalImages, r2ImageMap, ownMediaPublicUrls);
        collectHtmlImages(article.getContentHtml(), "contentHtml", externalImages, r2ImageMap, ownMediaPublicUrls);
        MediaAsset cover = article.getCoverMedia();
        addStoredImage("coverMedia.publicUrl", cover, externalImages, r2ImageMap, ownMediaPublicUrls);
        for (ArticleMedia bodyMedia : articleMedia.findByArticleIdAndRoleOrderBySortOrderAscIdAsc(article.getId(), "body")) {
            addStoredImage("bodyMedia.publicUrl", bodyMedia.getMediaAsset(), externalImages, r2ImageMap, ownMediaPublicUrls);
        }
        for (Map.Entry<String, String> field : legacyImageFields.entrySet()) {
            ImageUrlKind kind = imageUrlKind(field.getValue(), ownMediaPublicUrls);
            if (kind == ImageUrlKind.EXTERNAL) {
                externalImages.putIfAbsent(field.getValue(), new ImportedArticleImageReference(field.getKey(), field.getValue(), article.getTitle(), null, null));
            } else if (kind == ImageUrlKind.R2) {
                r2ImageMap.putIfAbsent(field.getValue(), new ImportedArticleImageReference(field.getKey(), field.getValue(), article.getTitle(), null, null));
            }
        }
        r2Images.addAll(r2ImageMap.values());
        return new ArrayList<>(externalImages.values());
    }

    private void addStoredImage(
            String field,
            MediaAsset mediaAsset,
            Map<String, ImportedArticleImageReference> externalImages,
            Map<String, ImportedArticleImageReference> r2Images,
            Set<String> ownMediaPublicUrls
    ) {
        if (mediaAsset == null || isBlank(mediaAsset.getPublicUrl())) {
            return;
        }
        ImportedArticleImageReference reference = new ImportedArticleImageReference(
                field, mediaAsset.getPublicUrl(), mediaAsset.getAltText(), mediaAsset.getCaption(), mediaAsset.getCredit()
        );
        ImageUrlKind kind = imageUrlKind(mediaAsset.getPublicUrl(), ownMediaPublicUrls);
        if (kind == ImageUrlKind.R2) {
            r2Images.putIfAbsent(mediaAsset.getPublicUrl(), reference);
        } else if (kind == ImageUrlKind.EXTERNAL) {
            externalImages.putIfAbsent(mediaAsset.getPublicUrl(), reference);
        }
    }

    private void collectMarkdownImages(
            String markdown,
            String field,
            Map<String, ImportedArticleImageReference> externalImages,
            Map<String, ImportedArticleImageReference> r2Images,
            Set<String> ownMediaPublicUrls
    ) {
        if (isBlank(markdown)) {
            return;
        }
        Matcher matcher = MARKDOWN_IMAGE.matcher(markdown);
        while (matcher.find()) {
            String url = matcher.group(2);
            ImageUrlKind kind = imageUrlKind(url, ownMediaPublicUrls);
            if (kind == ImageUrlKind.EXTERNAL) {
                externalImages.putIfAbsent(url, new ImportedArticleImageReference(field, url, matcher.group(1), null, null));
            } else if (kind == ImageUrlKind.R2) {
                r2Images.putIfAbsent(url, new ImportedArticleImageReference(field, url, matcher.group(1), null, null));
            }
        }
    }

    private void collectHtmlImages(
            String html,
            String field,
            Map<String, ImportedArticleImageReference> externalImages,
            Map<String, ImportedArticleImageReference> r2Images,
            Set<String> ownMediaPublicUrls
    ) {
        if (isBlank(html)) {
            return;
        }
        for (Element image : Jsoup.parseBodyFragment(html).select("img[src]")) {
            String url = image.attr("src").trim();
            ImageUrlKind kind = imageUrlKind(url, ownMediaPublicUrls);
            ImportedArticleImageReference reference = new ImportedArticleImageReference(
                    field, url, image.attr("alt").trim(), image.attr("data-caption").trim(), image.attr("data-credit").trim()
            );
            if (kind == ImageUrlKind.EXTERNAL) {
                externalImages.putIfAbsent(url, reference);
            } else if (kind == ImageUrlKind.R2) {
                r2Images.putIfAbsent(url, reference);
            }
        }
    }

    private ImageImportResult importExternalImages(Article article, List<ImportedArticleImageReference> references, boolean clearHtml) {
        String markdown = article.getContentMarkdown();
        if (isBlank(markdown) && !isBlank(article.getContentHtml())) {
            markdown = htmlToMarkdown.convert(article.getContentHtml());
        }
        if (markdown == null) {
            markdown = "";
        }
        List<String> notes = new ArrayList<>();
        int imported = 0;
        boolean changed = false;
        int sortOrder = articleMedia.countByArticleIdAndRole(article.getId(), "body");

        for (ImportedArticleImageReference reference : references) {
            try {
                String role = reference.field().startsWith("cover") ? "cover" : "body";
                String filenameBase = "cover".equals(role) ? "cover" : "body-" + (sortOrder + 1);
                MediaStorageService.StoredImage stored = mediaStorage.importArticleImage(article.getSlug(), filenameBase, reference.url());
                MediaAsset mediaAsset = new MediaAsset();
                mediaAsset.applyStoredImage(
                        stored.storageProvider(), stored.storagePath(), stored.publicUrl(), stored.filename(),
                        stored.mimeType(), stored.sizeBytes(), stored.width(), stored.height(),
                        fallback(reference.altText(), article.getTitle()), blankToNull(reference.caption()), blankToNull(reference.credit()),
                        reference.url(), null
                );
                MediaAsset saved = mediaAssets.save(mediaAsset);
                if ("cover".equals(role)) {
                    article.updateCoverMedia(saved);
                } else {
                    articleMedia.save(ArticleMedia.create(article, saved, "body", sortOrder));
                    sortOrder++;
                }
                markdown = replaceImageUrl(markdown, reference, stored.publicUrl());
                imported++;
                changed = true;
            } catch (ResponseStatusException | IllegalArgumentException exception) {
                notes.add("image import failed for " + reference.url() + ": " + exception.getMessage());
            }
        }
        return new ImageImportResult(markdown, imported, changed, notes);
    }

    private String replaceImageUrl(String markdown, ImportedArticleImageReference reference, String publicUrl) {
        String replaced = markdown.replace(reference.url(), publicUrl);
        if (replaced.equals(markdown) && "contentHtml".equals(reference.field())) {
            String caption = blankToNull(reference.caption());
            String credit = blankToNull(reference.credit());
            String line = "![" + fallback(reference.altText(), "") + "](" + publicUrl + ")";
            if (caption != null || credit != null) {
                line += "\n*" + fallback(caption, "") + (credit == null ? "" : " Credito: " + credit) + ".*";
            }
            replaced = markdown + (markdown.endsWith("\n") ? "\n" : "\n\n") + line + "\n";
        }
        return replaced;
    }

    private boolean applyExistingImageMetadata(Article article, boolean apply) {
        MediaAsset mediaAsset = existingImageForMetadata(article);
        if (mediaAsset == null) {
            return false;
        }
        if (!apply) {
            return true;
        }
        mediaAsset.updateEditorialMetadata(
                fallback(properties.articleNormalizeAltText(), mediaAsset.getAltText()),
                fallback(properties.articleNormalizeCaption(), mediaAsset.getCaption()),
                fallback(properties.articleNormalizeCredit(), mediaAsset.getCredit()),
                fallback(properties.articleNormalizeSourceUrl(), mediaAsset.getSourceUrl()),
                fallback(properties.articleNormalizeRightsNotes(), mediaAsset.getRightsNotes())
        );
        mediaAssets.save(mediaAsset);
        return true;
    }

    private MediaAsset existingImageForMetadata(Article article) {
        String role = properties.articleNormalizeImageRole() == null || properties.articleNormalizeImageRole().isBlank()
                ? "cover"
                : properties.articleNormalizeImageRole().trim().toLowerCase(Locale.ROOT);
        if ("cover".equals(role)) {
            return article.getCoverMedia();
        }
        if ("body".equals(role)) {
            List<ArticleMedia> bodyImages = articleMedia.findByArticleIdAndRoleOrderBySortOrderAscIdAsc(article.getId(), "body");
            return bodyImages.size() == 1 ? bodyImages.getFirst().getMediaAsset() : null;
        }
        return null;
    }

    private boolean propertiesMatch(Article article) {
        return properties.articleNormalizeArticleId() != null && properties.articleNormalizeArticleId().equals(article.getId());
    }

    private boolean r2LegalReviewPending(Article article, Set<String> ownMediaPublicUrls) {
        MediaAsset cover = article.getCoverMedia();
        if (legalMetadataIncomplete(cover) && imageUrlKind(cover.getPublicUrl(), ownMediaPublicUrls) == ImageUrlKind.R2) {
            return true;
        }
        return articleMedia.findByArticleIdAndRoleOrderBySortOrderAscIdAsc(article.getId(), "body").stream()
                .map(ArticleMedia::getMediaAsset)
                .anyMatch(media -> legalMetadataIncomplete(media) && imageUrlKind(media.getPublicUrl(), ownMediaPublicUrls) == ImageUrlKind.R2);
    }

    private boolean legalMetadataIncomplete(MediaAsset mediaAsset) {
        return mediaAsset != null
                && (isBlank(mediaAsset.getSourceUrl()) || isBlank(mediaAsset.getCredit()) || isBlank(mediaAsset.getRightsNotes()));
    }

    private boolean externalLegalReviewPending(List<ImportedArticleImageReference> externalImages) {
        return externalImages.stream().anyMatch(image -> isBlank(image.credit()));
    }

    private ImportedArticleNormalizeReport report(
            String action,
            boolean apply,
            List<Article> allArticles,
            List<ImportedArticleNormalizeReport.ArticleEntry> entries,
            int converted,
            int imported,
            int metadataUpdated
    ) {
        return new ImportedArticleNormalizeReport(
                OffsetDateTime.now().toString(),
                apply,
                action,
                allArticles.size(),
                countStatus(allArticles, ArticleStatus.draft),
                countStatus(allArticles, ArticleStatus.review),
                countStatus(allArticles, ArticleStatus.published),
                count(entries, "listo para Markdown"),
                count(entries, "necesita conversion HTML -> Markdown"),
                count(entries, "imagen ya en R2"),
                count(entries, "imagen externa recuperable"),
                count(entries, "no tiene imagen"),
                count(entries, "imagen en R2 con revision legal pendiente"),
                count(entries, "imagen con metadatos legales incompletos"),
                count(entries, "necesita revision manual"),
                converted,
                imported,
                metadataUpdated,
                entries
        );
    }

    private ImportedArticleNormalizeReport.ArticleEntry entry(Article article, Analysis analysis) {
        return new ImportedArticleNormalizeReport.ArticleEntry(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getStatus().name(),
                analysis.classifications(),
                analysis.r2Images().stream().map(image -> image.field() + "=" + image.url()).toList(),
                analysis.externalImages().stream().map(image -> image.field() + "=" + image.url()).toList(),
                analysis.notes()
        );
    }

    private Map<Long, Map<String, String>> legacyImageColumns(List<Article> allArticles) {
        List<?> columnRows = entityManager.createNativeQuery("""
                        select column_name
                        from information_schema.columns
                        where table_name = 'articles'
                        """)
                .getResultList();
        LinkedHashSet<String> existingColumns = new LinkedHashSet<>(columnRows.stream()
                .map(Object::toString)
                .toList());
        List<String> imageColumns = LEGACY_IMAGE_COLUMNS.stream()
                .filter(existingColumns::contains)
                .toList();
        Map<Long, Map<String, String>> values = new LinkedHashMap<>();
        if (imageColumns.isEmpty() || allArticles.isEmpty()) {
            return values;
        }
        for (Article article : allArticles) {
            String sql = "select " + String.join(", ", imageColumns) + " from articles where id = " + article.getId();
            Object row = entityManager.createNativeQuery(sql).getSingleResult();
            Object[] columns = row instanceof Object[] array ? array : new Object[]{row};
            Map<String, String> fields = new LinkedHashMap<>();
            for (int i = 0; i < imageColumns.size(); i++) {
                if (columns[i] != null) {
                    fields.put(imageColumns.get(i), columns[i].toString());
                }
            }
            values.put(article.getId(), fields);
        }
        return values;
    }

    private Set<String> ownMediaPublicUrls() {
        Set<String> urls = new LinkedHashSet<>();
        for (MediaAsset mediaAsset : mediaAssets.findAll()) {
            if (!isBlank(mediaAsset.getPublicUrl())) {
                urls.add(normalizeUrl(mediaAsset.getPublicUrl()));
            }
        }
        return urls;
    }

    private ImageUrlKind imageUrlKind(String value, Set<String> ownMediaPublicUrls) {
        if (isBlank(value)) {
            return ImageUrlKind.NONE;
        }
        try {
            URI uri = URI.create(value.trim());
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                return ImageUrlKind.NONE;
            }
            if (startsWithConfiguredPublicBase(value) || ownMediaPublicUrls.contains(normalizeUrl(value))) {
                return ImageUrlKind.R2;
            }
            return ImageUrlKind.EXTERNAL;
        } catch (IllegalArgumentException exception) {
            return ImageUrlKind.NONE;
        }
    }

    private boolean startsWithConfiguredPublicBase(String value) {
        return startsWith(value, storageProperties.publicBaseUrl()) || startsWith(value, storageProperties.s3PublicBaseUrl());
    }

    private boolean startsWith(String value, String prefix) {
        return !isBlank(prefix) && value.startsWith(prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix);
    }

    private String normalizeUrl(String value) {
        return value == null ? "" : value.trim();
    }

    private int countStatus(List<Article> allArticles, ArticleStatus status) {
        return (int) allArticles.stream().filter(article -> article.getStatus() == status).count();
    }

    private int count(List<ImportedArticleNormalizeReport.ArticleEntry> entries, String classification) {
        return (int) entries.stream().filter(entry -> entry.classifications().contains(classification)).count();
    }

    private String fallback(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record Analysis(
            List<String> classifications,
            List<String> notes,
            List<ImportedArticleImageReference> r2Images,
            List<ImportedArticleImageReference> externalImages,
            boolean needsHtmlConversion
    ) {
    }

    private record ImageImportResult(String markdown, int imported, boolean changed, List<String> notes) {
    }

    private enum ImageUrlKind {
        NONE,
        R2,
        EXTERNAL
    }
}
