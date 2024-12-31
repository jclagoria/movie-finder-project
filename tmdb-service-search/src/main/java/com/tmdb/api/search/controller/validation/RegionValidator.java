package com.tmdb.api.search.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegionValidator implements ConstraintValidator<ValidRegion, String> {

    private static final String REGION_REGEX = "^[A-Z]{2}$";

    @Override
    public boolean isValid(String sRegion, ConstraintValidatorContext context) {

        if (sRegion == null || sRegion.isBlank()) {
            return true;
        }

        if (!sRegion.matches(REGION_REGEX)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Region must be a 2-letter country code.").addConstraintViolation();
            return false;
        }

        return true;
    }
}
