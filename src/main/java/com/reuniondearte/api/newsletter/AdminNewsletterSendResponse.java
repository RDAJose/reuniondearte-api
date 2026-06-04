package com.reuniondearte.api.newsletter;

public record AdminNewsletterSendResponse(int sent, int failed, int skipped) {
}
