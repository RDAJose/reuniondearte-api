package com.reuniondearte.api.importer.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "normalize-imported-articles", havingValue = "true")
class ImportedArticleNormalizeRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ImportedArticleNormalizeRunner.class);

    private final ImportedArticleNormalizeProperties properties;
    private final ImportedArticleNormalizer normalizer;
    private final ImportedArticleNormalizeReportWriter reportWriter;
    private final ConfigurableApplicationContext context;

    ImportedArticleNormalizeRunner(
            ImportedArticleNormalizeProperties properties,
            ImportedArticleNormalizer normalizer,
            ImportedArticleNormalizeReportWriter reportWriter,
            ConfigurableApplicationContext context
    ) {
        this.properties = properties;
        this.normalizer = normalizer;
        this.reportWriter = reportWriter;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String action = properties.action();
        if (!"report".equals(action)
                && !"convert".equals(action)
                && !"images".equals(action)
                && !"metadata".equals(action)
                && !"plain-text-report".equals(action)
                && !"plain-text-convert".equals(action)) {
            throw new IllegalArgumentException("Unsupported --app.articleNormalizeAction. Use report, convert, images, metadata, plain-text-report or plain-text-convert.");
        }
        if ("metadata".equals(action) && properties.articleNormalizeArticleId() == null) {
            throw new IllegalArgumentException("Metadata update requires --app.articleNormalizeArticleId");
        }

        boolean apply = properties.articleNormalizeApply();
        log.info("Starting imported article normalization. action={} apply={}", action, apply);
        ImportedArticleNormalizeReport report = normalizer.run(action, apply, properties.articleNormalizeClearHtml());
        ImportedArticleNormalizeReportWriter.WrittenReport written = reportWriter.write(report);

        log.info("Imported article normalization finished. total={} draft={} review={} published={} readyMarkdown={} needsHtmlConversion={} r2Images={} externalImages={} withoutImage={} r2LegalReviewPending={} legalReviewPending={} manualReview={} converted={} plainTextConvertible={} plainTextManualReview={} plainTextTooShort={} plainTextPersonalOrNonEditorial={} plainTextConverted={} imagesImported={} metadataUpdated={} json={} summary={}",
                report.total(),
                report.draft(),
                report.review(),
                report.published(),
                report.readyMarkdown(),
                report.needsHtmlConversion(),
                report.withR2Images(),
                report.withExternalImages(),
                report.withoutImage(),
                report.r2LegalReviewPending(),
                report.legalReviewPending(),
                report.manualReview(),
                report.converted(),
                report.plainTextConvertible(),
                report.plainTextManualReview(),
                report.plainTextTooShort(),
                report.plainTextPersonalOrNonEditorial(),
                report.plainTextConverted(),
                report.imagesImported(),
                report.metadataUpdated(),
                written.json(),
                written.summary());

        int exitCode = org.springframework.boot.SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}
