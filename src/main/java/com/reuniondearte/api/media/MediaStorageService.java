package com.reuniondearte.api.media;

import com.reuniondearte.api.config.StorageProperties;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    private final StorageProperties storageProperties;

    public MediaStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
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
        Path mediaRoot = Path.of(storageProperties.mediaRoot()).toAbsolutePath().normalize();
        Path articleDirectory = mediaRoot.resolve(Path.of("articles", safeSlug)).normalize();
        Path target = articleDirectory.resolve(filename).normalize();
        if (!target.startsWith(mediaRoot)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid media path");
        }

        try {
            Files.createDirectories(articleDirectory);
            Path temp = Files.createTempFile(articleDirectory, "cover-", ".upload");
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveFailure) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image file", exception);
        }

        String storagePath = "articles/" + safeSlug + "/" + filename;
        String publicUrl = publicBaseUrl() + "/media/" + storagePath;
        return new StoredImage(
                storagePath,
                publicUrl,
                filename,
                expectedMimeType,
                file.getSize(),
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

    private String publicBaseUrl() {
        String baseUrl = storageProperties.publicBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:8080";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String normalize(String contentType) {
        return contentType == null || contentType.isBlank() ? null : contentType.toLowerCase(Locale.ROOT);
    }

    private record ImageSize(Integer width, Integer height) {
    }

    public record StoredImage(
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
