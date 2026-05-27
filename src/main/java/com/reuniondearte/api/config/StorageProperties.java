package com.reuniondearte.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rda")
public record StorageProperties(
        String storageProvider,
        String mediaRoot,
        String backupRoot,
        String importLogRoot,
        String publicBaseUrl,
        String allowedOrigins,
        String s3Endpoint,
        String s3Region,
        String s3Bucket,
        String s3AccessKey,
        String s3SecretKey,
        String s3PublicBaseUrl
) {
}
