package com.reuniondearte.api.media;

import com.reuniondearte.api.config.StorageProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "rda", name = "storage-provider", havingValue = "local", matchIfMissing = true)
public class LocalMediaStorageProvider implements MediaStorageProvider {
    private final StorageProperties storageProperties;

    public LocalMediaStorageProvider(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public String providerName() {
        return "local";
    }

    @Override
    public StoredObject store(String storagePath, String filename, String mimeType, long sizeBytes, InputStream inputStream) throws IOException {
        Path mediaRoot = Path.of(storageProperties.mediaRoot()).toAbsolutePath().normalize();
        Path target = mediaRoot.resolve(storagePath).normalize();
        if (!target.startsWith(mediaRoot)) {
            throw new IOException("Invalid media path");
        }

        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), "media-", ".upload");
        Files.copy(inputStream, temp, StandardCopyOption.REPLACE_EXISTING);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveFailure) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return new StoredObject(providerName(), storagePath, publicBaseUrl() + "/media/" + storagePath, filename, mimeType, sizeBytes);
    }

    private String publicBaseUrl() {
        String baseUrl = storageProperties.publicBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:8080";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
