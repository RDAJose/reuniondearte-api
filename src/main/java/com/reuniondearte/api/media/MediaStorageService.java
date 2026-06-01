package com.reuniondearte.api.media;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaStorageService {
    private static final long MAX_UPLOAD_BYTES = 8L * 1024L * 1024L;
    private static final long MAX_AUDIO_UPLOAD_BYTES = 50L * 1024L * 1024L;
    private static final long MAX_VIDEO_UPLOAD_BYTES = 150L * 1024L * 1024L;
    private static final Map<String, String> MIME_BY_EXTENSION = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp",
            "avif", "image/avif"
    );
    private static final Map<String, String> EXTENSION_BY_MIME = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp",
            "image/avif", "avif"
    );
    private static final Map<String, String> BODY_MEDIA_MIME_BY_EXTENSION = Map.ofEntries(
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("png", "image/png"),
            Map.entry("webp", "image/webp"),
            Map.entry("mp3", "audio/mpeg"),
            Map.entry("wav", "audio/wav"),
            Map.entry("ogg", "audio/ogg"),
            Map.entry("m4a", "audio/mp4"),
            Map.entry("mp4", "video/mp4"),
            Map.entry("webm", "video/webm")
    );
    private static final Set<String> BODY_IMAGE_MIMES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> BODY_AUDIO_MIMES = Set.of("audio/mpeg", "audio/wav", "audio/ogg", "audio/mp4");
    private static final Set<String> BODY_VIDEO_MIMES = Set.of("video/mp4", "video/webm");

    private final MediaStorageProvider storageProvider;
    private final HttpClient httpClient;

    public MediaStorageService(MediaStorageProvider storageProvider) {
        this.storageProvider = storageProvider;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public StoredImage storeArticleCover(String articleSlug, MultipartFile file) {
        return storeArticleImage(articleSlug, "cover", file);
    }

    public StoredImage storeArticleImage(String articleSlug, String filenameBase, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image file exceeds 8 MB");
        }

        String extension = extension(file.getOriginalFilename());
        String expectedMimeType = MIME_BY_EXTENSION.get(extension);
        if (expectedMimeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only jpg, jpeg, png, webp and avif images are allowed");
        }
        String contentType = normalize(file.getContentType());
        if (contentType != null && !expectedMimeType.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image MIME type does not match file extension");
        }

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded image", exception);
        }
        ImageSize imageSize = readImageSize(imageBytes, extension);
        return storeImageBytes(articleSlug, safeFilenameBase(filenameBase), extension, expectedMimeType, imageBytes, imageSize);
    }

    public StoredMedia storeArticleBodyMedia(String articleSlug, String filenameBase, MultipartFile file) {
        return storeArticleBodyMedia(articleSlug, filenameBase, file, Set.of("image", "audio", "video"));
    }

    public StoredMedia storeArticleBodyImage(String articleSlug, String filenameBase, MultipartFile file) {
        return storeArticleBodyMedia(articleSlug, filenameBase, file, Set.of("image"));
    }

    private StoredMedia storeArticleBodyMedia(String articleSlug, String filenameBase, MultipartFile file, Set<String> allowedMediaTypes) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Media file is required");
        }
        String extension = extension(file.getOriginalFilename());
        String expectedMimeType = BODY_MEDIA_MIME_BY_EXTENSION.get(extension);
        if (expectedMimeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only jpg, jpeg, png, webp, mp3, wav, ogg, m4a, mp4 and webm files are allowed");
        }
        String contentType = normalize(file.getContentType());
        if (contentType != null && !expectedMimeType.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Media MIME type does not match file extension");
        }
        String mediaType = bodyMediaType(expectedMimeType);
        if (!allowedMediaTypes.contains(mediaType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported media type for this endpoint");
        }
        long maxBytes = maxBytesFor(mediaType);
        if (file.getSize() > maxBytes) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Media file exceeds " + (maxBytes / 1024 / 1024) + " MB");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded media", exception);
        }
        if ("image".equals(mediaType)) {
            ImageSize imageSize = readImageSize(bytes, extension);
            StoredImage storedImage = storeImageBytes(articleSlug, safeFilenameBase(filenameBase), extension, expectedMimeType, bytes, imageSize);
            return StoredMedia.fromImage(storedImage);
        }
        return storeMediaBytes(articleSlug, safeFilenameBase(filenameBase), extension, expectedMimeType, bytes, mediaType);
    }

    public StoredImage importArticleImage(String articleSlug, String filenameBase, String imageUrl) {
        URI uri = validatedHttpUri(imageUrl);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "image/jpeg,image/png,image/webp,image/avif")
                .GET()
                .build();

        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not download image", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image download was interrupted", exception);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image URL did not return a successful response");
        }

        String contentType = normalize(response.headers().firstValue("content-type").orElse(null));
        if (contentType != null && contentType.contains(";")) {
            contentType = contentType.substring(0, contentType.indexOf(';')).trim();
        }
        String extension = EXTENSION_BY_MIME.get(contentType);
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Remote image must be jpg, png, webp or avif");
        }

        long declaredLength = response.headers()
                .firstValueAsLong("content-length")
                .orElse(response.body().length);
        if (declaredLength > MAX_UPLOAD_BYTES || response.body().length > MAX_UPLOAD_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image file exceeds 8 MB");
        }

        ImageSize imageSize = readImageSize(response.body(), extension);
        return storeImageBytes(articleSlug, safeFilenameBase(filenameBase), extension, contentType, response.body(), imageSize);
    }

    private StoredImage storeImageBytes(
            String articleSlug,
            String filenameBase,
            String extension,
            String mimeType,
            byte[] imageBytes,
            ImageSize imageSize
    ) {
        String safeSlug = safeSlug(articleSlug);
        String filename = filenameBase + "." + extension;
        String storagePath = "articles/" + safeSlug + "/" + filename;
        MediaStorageProvider.StoredObject storedObject;
        try {
            try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                storedObject = storageProvider.store(storagePath, filename, mimeType, imageBytes.length, inputStream);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image file", exception);
        }

        return new StoredImage(
                storedObject.storageProvider(),
                storedObject.storagePath(),
                storedObject.publicUrl(),
                storedObject.filename(),
                storedObject.mimeType(),
                storedObject.sizeBytes(),
                imageSize.width(),
                imageSize.height()
        );
    }

    private StoredMedia storeMediaBytes(
            String articleSlug,
            String filenameBase,
            String extension,
            String mimeType,
            byte[] bytes,
            String mediaType
    ) {
        String safeSlug = safeSlug(articleSlug);
        String filename = filenameBase + "." + extension;
        String storagePath = "articles/" + safeSlug + "/" + filename;
        MediaStorageProvider.StoredObject storedObject;
        try {
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                storedObject = storageProvider.store(storagePath, filename, mimeType, bytes.length, inputStream);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store media file", exception);
        }

        return new StoredMedia(
                mediaType,
                storedObject.storageProvider(),
                storedObject.storagePath(),
                storedObject.publicUrl(),
                storedObject.filename(),
                storedObject.mimeType(),
                storedObject.sizeBytes(),
                null,
                null
        );
    }

    private String bodyMediaType(String mimeType) {
        if (BODY_IMAGE_MIMES.contains(mimeType)) {
            return "image";
        }
        if (BODY_AUDIO_MIMES.contains(mimeType)) {
            return "audio";
        }
        if (BODY_VIDEO_MIMES.contains(mimeType)) {
            return "video";
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported media type");
    }

    private long maxBytesFor(String mediaType) {
        if ("audio".equals(mediaType)) {
            return MAX_AUDIO_UPLOAD_BYTES;
        }
        if ("video".equals(mediaType)) {
            return MAX_VIDEO_UPLOAD_BYTES;
        }
        return MAX_UPLOAD_BYTES;
    }

    private ImageSize readImageSize(byte[] imageBytes, String extension) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                if ("webp".equals(extension) || "avif".equals(extension)) {
                    return new ImageSize(null, null);
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is not a valid image");
            }
            return new ImageSize(image.getWidth(), image.getHeight());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read image", exception);
        }
    }

    private String extension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image filename is required");
        }
        String filename = originalFilename.replace("\\", "/");
        int slashIndex = filename.lastIndexOf('/');
        if (slashIndex >= 0) {
            filename = filename.substring(slashIndex + 1);
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == filename.length() - 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file extension is required");
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String safeSlug(String slug) {
        if (slug == null || slug.isBlank() || !slug.matches("[a-z0-9][a-z0-9-]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article slug is not safe for media storage");
        }
        return slug;
    }

    private String safeFilenameBase(String value) {
        if (value == null || value.isBlank() || !value.matches("[a-z0-9][a-z0-9-]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image filename is not safe for media storage");
        }
        return value;
    }

    private URI validatedHttpUri(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image URL is required");
        }
        URI uri;
        try {
            uri = URI.create(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image URL is invalid", exception);
        }
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image URL must use http or https");
        }
        return uri;
    }

    private String normalize(String contentType) {
        return contentType == null || contentType.isBlank() ? null : contentType.toLowerCase(Locale.ROOT);
    }

    private record ImageSize(Integer width, Integer height) {
    }

    public record StoredImage(
            String storageProvider,
            String storagePath,
            String publicUrl,
            String filename,
            String mimeType,
            Long sizeBytes,
            Integer width,
            Integer height
    ) {
    }

    public record StoredMedia(
            String mediaType,
            String storageProvider,
            String storagePath,
            String publicUrl,
            String filename,
            String mimeType,
            Long sizeBytes,
            Integer width,
            Integer height
    ) {
        static StoredMedia fromImage(StoredImage image) {
            return new StoredMedia(
                    "image",
                    image.storageProvider(),
                    image.storagePath(),
                    image.publicUrl(),
                    image.filename(),
                    image.mimeType(),
                    image.sizeBytes(),
                    image.width(),
                    image.height()
            );
        }
    }
}
