package com.reuniondearte.api.media;

import com.reuniondearte.api.config.StorageProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@ConditionalOnProperty(prefix = "rda", name = "storage-provider", havingValue = "s3")
public class S3MediaStorageProvider implements MediaStorageProvider {
    private final StorageProperties storageProperties;
    private final S3Client s3Client;

    public S3MediaStorageProvider(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        validateConfiguration(storageProperties);
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(storageProperties.s3Endpoint()))
                .region(Region.of(storageProperties.s3Region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(storageProperties.s3AccessKey(), storageProperties.s3SecretKey())
                ))
                .forcePathStyle(true)
                .build();
    }

    @Override
    public String providerName() {
        return "s3";
    }

    @Override
    public StoredObject store(String storagePath, String filename, String mimeType, long sizeBytes, InputStream inputStream) throws IOException {
        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(storageProperties.s3Bucket())
                .key(storagePath)
                .contentType(mimeType)
                .contentLength(sizeBytes);
        String cacheControl = storageProperties.s3CacheControl();
        if (!isBlank(cacheControl)) {
            requestBuilder.cacheControl(cacheControl.trim());
        }
        PutObjectRequest request = requestBuilder.build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, sizeBytes));
        return new StoredObject(providerName(), storagePath, publicBaseUrl() + "/" + storagePath, filename, mimeType, sizeBytes);
    }

    private void validateConfiguration(StorageProperties properties) {
        if (isBlank(properties.s3Endpoint())
                || isBlank(properties.s3Region())
                || isBlank(properties.s3Bucket())
                || isBlank(properties.s3AccessKey())
                || isBlank(properties.s3SecretKey())
                || isBlank(properties.s3PublicBaseUrl())) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 storage is not fully configured");
        }
    }

    private String publicBaseUrl() {
        String baseUrl = storageProperties.s3PublicBaseUrl();
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
