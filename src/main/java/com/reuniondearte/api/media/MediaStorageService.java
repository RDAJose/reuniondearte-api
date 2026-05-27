package com.reuniondearte.api.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaStorageService {
    private static final long MAX_UPLOAD_BYTES = 8L * 1024L * 1024L;
    private static final Map<String, String> MIME_BY_EXTENSION = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp"
    );

    private final MediaStorageProvider storageProvider;

    public MediaStorageService(MediaStorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public StoredImage storeArticleCover(String articleSlug, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image file exceeds 8 MB");
        }

        String extension = extension(file.getOriginalFilename());
        String expectedMimeType = MIME_BY_EXTENSION.get(extension);
        if (expectedMimeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only jpg, jpeg, png and webp images are allowed");
        }
        String contentType = normalize(file.getContentType());
        if (contentType != null && !expectedMimeType.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image MIME type does not match file extension");
        }

        ImageSize imageSize = readImageSize(file, extension);
        String safeSlug = safeSlug(articleSlug);
        String filename = "cover." + extension;
        String storagePath = "articles/" + safeSlug + "/" + filename;
        MediaStorageProvider.StoredObject storedObject;
        try {
            try (InputStream inputStream = file.getInputStream()) {
                storedObject = storageProvider.store(storagePath, filename, expectedMimeType, file.getSize(), inputStream);
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

    private ImageSize readImageSize(MultipartFile file, String extension) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                if ("webp".equals(extension)) {
                    return new ImageSize(null, null);
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not a valid image");
            }
            return new ImageSize(image.getWidth(), image.getHeight());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded image", exception);
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
}
