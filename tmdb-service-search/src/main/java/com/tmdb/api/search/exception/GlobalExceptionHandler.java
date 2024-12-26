package com.tmdb.api.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(error ->
               error.getResolvableErrors().forEach( reolveError ->
                       errors.put("MessageError",reolveError.getDefaultMessage())
        ));

        errors.put("error", "Validation failed for one or more parameters");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestValueException(MissingRequestValueException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Validation failed for one or more parameters");
        errors.put(ex.getName(), ex.getReason());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleServerWebInputException(ServerWebInputException ex) {
        String parameterName = Objects.requireNonNull(ex.getMethodParameter()).getParameterName();
        String errorMessage = ex.getCause().getLocalizedMessage();

        Map<String, String> errors = new HashMap<>();
        errors.put("Parameter Name", parameterName);
        errors.put("Message", errorMessage);
        errors.put("error", "Validation failed for one or more parameters");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiExternalException> handleWebClientResponseException(WebClientResponseException webEx) {
        ApiExternalException apiEx = new ApiExternalException(webEx.getMessage(), webEx,
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(apiEx, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApplicationException> handleRuntimeException(Exception ex) {
        ApplicationException apiEx = new ApplicationException(ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(apiEx, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationException> handleGeneralException(Exception ex) {
        ApplicationException apiEx = new ApplicationException(ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(apiEx, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
