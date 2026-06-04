package com.reuniondearte.api.newsletter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NewsletterStartupLogger implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(NewsletterStartupLogger.class);
    public static final String VERSION = "newsletter-v2-error-json-2026-06-04";

    @Override
    public void run(ApplicationArguments args) {
        log.info("newsletter system active version={}", VERSION);
    }
}
