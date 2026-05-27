package com.reuniondearte.api.importer.legacy;

import com.reuniondearte.api.config.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@ConditionalOnProperty(prefix = "app", name = "scan-legacy", havingValue = "true")
public class LegacyScanRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LegacyScanRunner.class);
    private final LegacyScanProperties properties;
    private final StorageProperties storageProperties;
    private final LegacyHtmlScanner scanner;
    private final LegacyReportWriter reportWriter;
    private final ConfigurableApplicationContext context;

    public LegacyScanRunner(
            LegacyScanProperties properties,
            StorageProperties storageProperties,
            LegacyHtmlScanner scanner,
            LegacyReportWriter reportWriter,
            ConfigurableApplicationContext context
    ) {
        this.properties = properties;
        this.storageProperties = storageProperties;
        this.scanner = scanner;
        this.reportWriter = reportWriter;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (properties.legacyRoot() == null || properties.legacyRoot().isBlank()) {
            throw new IllegalArgumentException("Missing --app.legacyRoot");
        }

        Path legacyRoot = Path.of(properties.legacyRoot()).toAbsolutePath().normalize();
        if (!Files.isDirectory(legacyRoot)) {
            throw new IllegalArgumentException("Legacy root is not a directory: " + legacyRoot);
        }

        Path outputRoot = Path.of(storageProperties.importLogRoot()).toAbsolutePath().normalize();
        log.info("Starting read-only legacy scan. root={} output={}", legacyRoot, outputRoot);
        LegacyScanReport report = scanner.scan(legacyRoot);
        LegacyReportWriter.WrittenReports writtenReports = reportWriter.write(report, outputRoot);
        log.info("Legacy scan finished. analyzed={} candidates={} discarded={} json={} summary={} discardedLog={}",
                report.htmlAnalyzed(),
                report.candidatesDetected(),
                report.discarded().size(),
                writtenReports.json(),
                writtenReports.summary(),
                writtenReports.discarded());

        int exitCode = org.springframework.boot.SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}
