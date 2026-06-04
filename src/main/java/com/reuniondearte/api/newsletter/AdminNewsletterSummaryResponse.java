package com.reuniondearte.api.newsletter;

import java.util.List;
import java.util.Map;

public record AdminNewsletterSummaryResponse(
        Map<NewsletterSubscriberStatus, Long> totals,
        List<AdminNewsletterSubscriberResponse> subscribers
) {
}
