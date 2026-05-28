package com.reuniondearte.api.importer.legacy;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record LegacyCandidateImportProperties(
        boolean importLegacyCandidates,
        String legacyCandidatesFile,
        boolean skipLegacyImportBackup
) {
}
