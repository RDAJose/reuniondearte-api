package com.reuniondearte.api.admin;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SafeAuthorUrlValidator.class)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeAuthorUrl {
    String message() default "URL must be empty or safe";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Kind kind();

    enum Kind {
        EXTERNAL,
        AVATAR
    }
}
