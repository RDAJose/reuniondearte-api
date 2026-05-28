package com.reuniondearte.api.importer.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
class ImportedArticleNormalizeReportWriter {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    WrittenReport write(ImportedArticleNormalizeReport report) throws IOException {
        Path outputRoot = Path.of("storage", "reports").toAbsolutePath().normalize();
        Files.createDirectories(outputRoot);
        String stamp = report.createdAt().replaceAll("[^0-9]", "").substring(0, 14);
        Path json = outputRoot.resolve("imported-articles-" + report.action() + "-" + stamp + ".json");
        Path summary = outputRoot.resolve("imported-articles-" + report.action() + "-" + stamp + ".md");
        objectMapper.writeValue(json.toFile(), report);
        Files.writeString(summary, summary(report));
        return new WrittenReport(json, summary);
    }

    private String summary(ImportedArticleNormalizeReport report) {
        StringBuilder text = new StringBuilder();
        text.append("# Imported article normalization report\n\n");
        text.append("- action: ").append(report.action()).append("\n");
        text.append("- apply: ").append(report.apply()).append("\n");
        text.append("- total: ").append(report.total()).append("\n");
        text.append("- draft/review/published: ")
                .append(report.draft()).append("/")
                .append(report.review()).append("/")
                .append(report.published()).append("\n");
        text.append("- ready Markdown: ").append(report.readyMarkdown()).append("\n");
        text.append("- needs HTML conversion: ").append(report.needsHtmlConversion()).append("\n");
        text.append("- with R2 images: ").append(report.withR2Images()).append("\n");
        text.append("- with external images: ").append(report.withExternalImages()).append("\n");
        text.append("- without image: ").append(report.withoutImage()).append("\n");
        text.append("- R2 legal review pending: ").append(report.r2LegalReviewPending()).append("\n");
        text.append("- legal review pending: ").append(report.legalReviewPending()).append("\n");
        text.append("- manual review: ").append(report.manualReview()).append("\n");
        text.append("- converted: ").append(report.converted()).append("\n");
        text.append("- images imported: ").append(report.imagesImported()).append("\n\n");
        text.append("- metadata updated: ").append(report.metadataUpdated()).append("\n\n");
        for (ImportedArticleNormalizeReport.ArticleEntry article : report.articles()) {
            text.append("## ").append(article.id()).append(" - ").append(article.title()).append("\n");
            text.append("- slug: ").append(article.slug()).append("\n");
            text.append("- status: ").append(article.status()).append("\n");
            text.append("- classifications: ").append(String.join(", ", article.classifications())).append("\n");
            if (!article.r2Images().isEmpty()) {
                text.append("- R2 images: ").append(String.join(", ", article.r2Images())).append("\n");
            }
            if (!article.externalImages().isEmpty()) {
                text.append("- external images: ").append(String.join(", ", article.externalImages())).append("\n");
            }
            if (!article.notes().isEmpty()) {
                text.append("- notes: ").append(String.join("; ", article.notes())).append("\n");
            }
            text.append("\n");
        }
        return text.toString();
    }

    record WrittenReport(Path json, Path summary) {
    }
}
