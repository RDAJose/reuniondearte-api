package com.reuniondearte.api.newsletter;

public class ConsentRequiredException extends RuntimeException {
    public ConsentRequiredException() {
        super("Consent is required");
    }
}
