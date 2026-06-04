package com.reuniondearte.api.config;

import com.reuniondearte.api.newsletter.ConsentRequiredException;
import com.reuniondearte.api.newsletter.EmailServiceUnavailableException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldErrorResponse> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(this::fieldError)
                .toList();
        String message = fields.isEmpty() ? "Request validation failed" : fields.getFirst().message();
        return ResponseEntity.badRequest().body(new ApiErrorResponse("VALIDATION_ERROR", message, fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> messageNotReadable(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of("INVALID_JSON", "Request body is missing or is not valid JSON"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> missingParameter(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                "MISSING_PARAMETER",
                "Missing required parameter: " + exception.getParameterName(),
                List.of(new FieldErrorResponse(exception.getParameterName(), "Parameter is required"))
        ));
    }

    @ExceptionHandler(ConsentRequiredException.class)
    public ResponseEntity<ApiErrorResponse> consentRequired(ConsentRequiredException exception) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of("CONSENT_REQUIRED", exception.getMessage()));
    }

    @ExceptionHandler(EmailServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> emailServiceUnavailable(EmailServiceUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErrorResponse.of("EMAIL_SERVICE_UNAVAILABLE", exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> responseStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return ResponseEntity.status(status).body(ApiErrorResponse.of(errorCode(status), exception.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> generic(Exception exception) {
        log.error("Unhandled API exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("INTERNAL_ERROR", "Unexpected server error"));
    }

    private FieldErrorResponse fieldError(FieldError error) {
        return new FieldErrorResponse(error.getField(), validationMessage(error));
    }

    private String validationMessage(FieldError error) {
        if ("email".equals(error.getField())) {
            return "Email is invalid";
        }
        return error.getDefaultMessage() == null ? "Field is invalid" : error.getDefaultMessage();
    }

    private String errorCode(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "BAD_REQUEST";
            case NOT_FOUND -> "NOT_FOUND";
            case CONFLICT -> "CONFLICT";
            case SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE";
            default -> "HTTP_" + status.value();
        };
    }
}
