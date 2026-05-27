package com.reuniondearte.api.importer.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;

@Component
public class LegacyReportWriter {
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault());
    private final ObjectMapper objectMapper;

    public LegacyReportWriter() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public WrittenReports write(LegacyScanReport report, Path outputRoot) throws IOException {
        Files.createDirectories(outputRoot);
        String prefix = "legacy-scan-" + FILE_TIMESTAMP.format(report.scannedAt());
        Path json = outputRoot.resolve(prefix + "-candidates.json");
        Path summary = outputRoot.resolve(prefix + "-summary.md");
        Path discarded = outputRoot.resolve(prefix + "-discarded.log");

        objectMapper.writeValue(json.toFile(), report.candidates());
        Files.writeString(summary, summary(report), StandardCharsets.UTF_8);
        Files.writeString(discarded, discarded(report), StandardCharsets.UTF_8);
        return new WrittenReports(json, summary, discarded);
    }

    private String summary(LegacyScanReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Legacy scan summary\n\n");
        builder.append("- Scanned at: ").append(report.scannedAt()).append('\n');
        builder.append("- Legacy root: ").append(report.legacyRoot()).append('\n');
        builder.append("- HTML analyzed: ").append(report.htmlAnalyzed()).append('\n');
        builder.append("- Candidates detected: ").append(report.candidatesDetected()).append('\n');
        builder.append("- Candidates with images: ").append(report.candidatesWithImages()).append('\n');
        builder.append("- Candidates with possible mojibake: ").append(report.candidatesWithPossibleMojibake()).append('\n');
        builder.append("\n## Discarded by reason\n\n");
        report.discardedByReason().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n'));
        builder.append("\n## Top candidates\n\n");
        report.candidates().stream()
                .sorted(Comparator.comparingInt(LegacyArticleCandidate::wordCount).reversed())
                .limit(20)
                .forEach(candidate -> builder
                        .append("- ").append(candidate.title())
                        .append(" | words=").append(candidate.wordCount())
                        .append(" | images=").append(candidate.images().size())
                        .append(" | path=").append(candidate.originalPath())
                        .append('\n'));
        return builder.toString();
    }

    private String discarded(LegacyScanReport report) {
        StringBuilder builder = new StringBuilder();
        for (LegacyScanReport.LegacyDiscardedPage page : report.discarded()) {
            builder.append(page.reason())
                    .append('\t')
                    .append(page.originalPath())
                    .append('\t')
                    .append(page.detail())
                    .append('\n');
        }
        return builder.toString();
    }

    public record WrittenReports(Path json, Path summary, Path discarded) {
    }
}
