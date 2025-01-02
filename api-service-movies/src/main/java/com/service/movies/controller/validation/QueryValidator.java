package com.service.movies.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QueryValidator implements ConstraintValidator<ValidQuery, String> {

    @Override
    public boolean isValid(String query, ConstraintValidatorContext constraintValidatorContext) {

        return query != null && !query.isBlank();
    }
}
