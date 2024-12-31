package com.tmdb.api.search.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidYear, String> {

    private static final String YEAR_REGEX = "^\\d{4}$";

    @Override
    public boolean isValid(String sYear, ConstraintValidatorContext context) {

        if (sYear == null || sYear.isBlank()) {
            return true;
        }

        if (!sYear.matches(YEAR_REGEX)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid year format. Expected 'YYYY'.").addConstraintViolation();
            return false;
        }

        return false;
    }
}
