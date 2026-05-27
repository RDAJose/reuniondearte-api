package com.reuniondearte.api.config;

import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "rda", name = "storage-provider", havingValue = "local", matchIfMissing = true)
public class MediaResourceConfig implements WebMvcConfigurer {
    private final StorageProperties storageProperties;

    public MediaResourceConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String mediaLocation = Path.of(storageProperties.mediaRoot())
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        registry.addResourceHandler("/media/**")
                .addResourceLocations(mediaLocation.endsWith("/") ? mediaLocation : mediaLocation + "/");
    }
}
