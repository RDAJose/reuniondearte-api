package com.reuniondearte.api;

import com.reuniondearte.api.config.StorageProperties;
import com.reuniondearte.api.importer.legacy.ImportedArticleNormalizeProperties;
import com.reuniondearte.api.importer.legacy.LegacyCandidateImportProperties;
import com.reuniondearte.api.importer.legacy.LegacyScanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		StorageProperties.class,
		LegacyScanProperties.class,
		LegacyCandidateImportProperties.class,
		ImportedArticleNormalizeProperties.class
})
public class ReuniondearteApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReuniondearteApiApplication.class, args);
	}

}
