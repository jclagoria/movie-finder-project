package com.tmdb.api.search.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to validate language codes in the format 'xx-XX'.
 * This allows null or blank values for optional fields but validates non-blank values.
 */
@Constraint(validatedBy = LanguageValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLanguage {
    String message() default "Invalid language format. Use 'xx-XX'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
