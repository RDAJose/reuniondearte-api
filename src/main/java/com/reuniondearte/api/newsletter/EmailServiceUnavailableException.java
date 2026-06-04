package com.reuniondearte.api.newsletter;

public class EmailServiceUnavailableException extends RuntimeException {
    public EmailServiceUnavailableException() {
        super("Email service is not configured or could not send confirmation");
    }

    public EmailServiceUnavailableException(String message) {
        super(message);
    }

    public EmailServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
