package com.reuniondearte.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rda")
public record StorageProperties(
        String mediaRoot,
        String backupRoot,
        String importLogRoot,
        String publicBaseUrl,
        String allowedOrigins
) {
}

