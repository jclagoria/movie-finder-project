package com.tmdb.api.search.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, String> {

    private static final String LANGUAGE_REGEX = "^[a-z]{2}-[A-Z]{2}$";

    @Override
    public boolean isValid(String sLanguage, ConstraintValidatorContext context) {

        if (sLanguage == null || sLanguage.isBlank()) {
            return true;
        }

        if (!sLanguage.matches(LANGUAGE_REGEX)) {
            // Customize the error message if the regex fails
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid language format. Expected 'xx-XX'.").addConstraintViolation();
            return false;
        }

        return true;
    }
}
