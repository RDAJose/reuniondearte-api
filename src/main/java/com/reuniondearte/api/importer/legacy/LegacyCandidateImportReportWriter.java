package com.reuniondearte.api.importer.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class LegacyCandidateImportReportWriter {
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault());
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public WrittenImportReports write(LegacyCandidateImportReport report, Path outputRoot) throws IOException {
        Files.createDirectories(outputRoot);
        String prefix = "legacy-import-" + FILE_TIMESTAMP.format(Instant.parse(report.importedAt()));
        Path json = outputRoot.resolve(prefix + "-report.json");
        Path summary = outputRoot.resolve(prefix + "-summary.md");
        objectMapper.writeValue(json.toFile(), report);
        Files.writeString(summary, summary(report), StandardCharsets.UTF_8);
        return new WrittenImportReports(json, summary);
    }

    private String summary(LegacyCandidateImportReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Legacy candidate import summary\n\n");
        builder.append("- Imported at: ").append(report.importedAt()).append('\n');
        builder.append("- Candidates file: ").append(report.candidatesFile()).append('\n');
        builder.append("- Backup path: ").append(report.backupPath()).append('\n');
        builder.append("- Candidates read: ").append(report.candidatesRead()).append('\n');
        builder.append("- Imported: ").append(report.imported()).append('\n');
        builder.append("- Updated same import path: ").append(report.updated()).append('\n');
        builder.append("- Skipped duplicates: ").append(report.skippedDuplicates()).append('\n');
        builder.append("- Failed: ").append(report.failed()).append('\n');
        builder.append("- With warnings: ").append(report.withWarnings()).append('\n');
        builder.append("- Referenced images in JSON: ").append(report.referencedImages()).append('\n');
        builder.append("\n## Categories used\n\n");
        report.categoriesUsed().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n'));
        builder.append("\n## Imported or updated articles\n\n");
        report.articles().stream().limit(80).forEach(article -> builder
                .append("- ").append(article.slug())
                .append(" | ").append(article.status())
                .append(" | ").append(article.category())
                .append(" | warnings=").append(article.warnings().size())
                .append(" | images=").append(article.referencedImages())
                .append('\n'));
        builder.append("\n## Skipped candidates\n\n");
        report.skipped().stream().limit(80).forEach(candidate -> builder
                .append("- ").append(candidate.slug())
                .append(" | ").append(candidate.reason())
                .append(" | ").append(candidate.originalPath())
                .append('\n'));
        return builder.toString();
    }

    public record WrittenImportReports(Path json, Path summary) {
    }
}
