package com.reuniondearte.api.media;

import java.io.IOException;
import java.io.InputStream;

public interface MediaStorageProvider {
    String providerName();

    StoredObject store(String storagePath, String filename, String mimeType, long sizeBytes, InputStream inputStream) throws IOException;

    record StoredObject(
            String storageProvider,
            String storagePath,
            String publicUrl,
            String filename,
            String mimeType,
            Long sizeBytes
    ) {
    }
}
