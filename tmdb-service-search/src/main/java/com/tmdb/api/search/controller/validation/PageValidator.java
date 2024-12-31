package com.tmdb.api.search.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PageValidator implements ConstraintValidator<ValidPage, Long> {

    @Override
    public boolean isValid(Long lValue, ConstraintValidatorContext constraintValidatorContext) {
        return lValue != null && lValue > 0;
    }
}
