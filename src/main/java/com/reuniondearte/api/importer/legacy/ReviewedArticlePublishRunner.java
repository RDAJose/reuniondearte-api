package com.reuniondearte.api.importer.legacy;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "publish-reviewed-articles", havingValue = "true")
class ReviewedArticlePublishRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ReviewedArticlePublishRunner.class);

    private final ImportedArticleNormalizeProperties properties;
    private final ReviewedArticlePublisher publisher;
    private final ConfigurableApplicationContext context;

    ReviewedArticlePublishRunner(
            ImportedArticleNormalizeProperties properties,
            ReviewedArticlePublisher publisher,
            ConfigurableApplicationContext context
    ) {
        this.properties = properties;
        this.publisher = publisher;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Long> requestedIds = properties.reviewedArticleIds() == null ? List.of() : properties.reviewedArticleIds();
        if (requestedIds.isEmpty() && !properties.publishReviewedConfirmed()) {
            throw new IllegalArgumentException("Publishing requires --app.reviewedArticleIds=1,2 or --app.publishReviewedConfirmed=true");
        }

        ReviewedArticlePublisher.PublishResult result = publisher.publish(requestedIds, properties.publishReviewedConfirmed());
        log.info("Reviewed article publish finished. requestedIds={} confirmedAllReview={} published={} skipped={}",
                result.requestedIds(), result.confirmedAllReview(), result.published(), result.skipped());

        int exitCode = org.springframework.boot.SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}
