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
@ConditionalOnProperty(prefix = "app", name = "import-legacy-candidates", havingValue = "true")
public class LegacyCandidateImportRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LegacyCandidateImportRunner.class);
    private final LegacyCandidateImportProperties properties;
    private final StorageProperties storageProperties;
    private final LegacyImportBackupService backupService;
    private final LegacyCandidateImporter importer;
    private final LegacyCandidateImportReportWriter reportWriter;
    private final ConfigurableApplicationContext context;

    public LegacyCandidateImportRunner(
            LegacyCandidateImportProperties properties,
            StorageProperties storageProperties,
            LegacyImportBackupService backupService,
            LegacyCandidateImporter importer,
            LegacyCandidateImportReportWriter reportWriter,
            ConfigurableApplicationContext context
    ) {
        this.properties = properties;
        this.storageProperties = storageProperties;
        this.backupService = backupService;
        this.importer = importer;
        this.reportWriter = reportWriter;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (properties.legacyCandidatesFile() == null || properties.legacyCandidatesFile().isBlank()) {
            throw new IllegalArgumentException("Missing --app.legacyCandidatesFile");
        }

        Path candidatesFile = Path.of(properties.legacyCandidatesFile()).toAbsolutePath().normalize();
        if (!Files.isRegularFile(candidatesFile)) {
            throw new IllegalArgumentException("Legacy candidates file not found: " + candidatesFile);
        }

        log.info("Creating PostgreSQL backup before legacy import");
        Path backupPath = backupService.createPostgresBackup();
        log.info("Backup created at {}", backupPath);

        LegacyCandidateImportReport report = importer.importCandidates(candidatesFile, backupPath.toString());
        Path outputRoot = Path.of(storageProperties.importLogRoot()).toAbsolutePath().normalize();
        LegacyCandidateImportReportWriter.WrittenImportReports writtenReports = reportWriter.write(report, outputRoot);

        log.info("Legacy candidate import finished. read={} imported={} updated={} skippedDuplicates={} failed={} warnings={} report={} summary={}",
                report.candidatesRead(),
                report.imported(),
                report.updated(),
                report.skippedDuplicates(),
                report.failed(),
                report.withWarnings(),
                writtenReports.json(),
                writtenReports.summary());

        int exitCode = org.springframework.boot.SpringApplication.exit(context, () -> report.failed() == 0 ? 0 : 1);
        System.exit(exitCode);
    }
}
