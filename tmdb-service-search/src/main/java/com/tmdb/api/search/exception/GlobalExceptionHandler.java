package com.tmdb.api.search.exception;

import com.tmdb.api.search.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(error ->
               error.getResolvableErrors().forEach( reolveError ->
                       errors.put("MessageError",reolveError.getDefaultMessage())
        ));

        errors.put("time", Instant.now().toString());
        errors.put("error", "Validation failed for one or more parameters");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestValueException(MissingRequestValueException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Validation failed for one or more parameters");
        errors.put(ex.getName(), ex.getReason());
        errors.put("time", Instant.now().toString());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleServerWebInputException(ServerWebInputException ex) {
        String parameterName = Objects.requireNonNull(ex.getMethodParameter()).getParameterName();
        String errorMessage = ex.getCause().getLocalizedMessage();

        Map<String, String> errors = new HashMap<>();
        errors.put("Parameter Name", parameterName);
        errors.put("Message", errorMessage);
        errors.put("time", Instant.now().toString());
        errors.put("error", "Validation failed for one or more parameters");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Validation failed for one or more parameters");
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        errors.put("time", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        errors.put("time", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if ("page".equals(ex.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid page number. Page must be a numeric value.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid request parameter: " + ex.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ApiExternalException.class)
    public ResponseEntity<ApiErrorResponse> handleWebClientResponseException(ApiExternalException ex) {
        logger.error("Handling WebClientResponseException: Status {} Body: {}", ex.isSuccess(), ex.getError());

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                ex.getTitleMessage(),
                ex.isSuccess(),
                ex.getError(),
                ex.getStatusCode(),
                Instant.now().toString());

        return ResponseEntity.status(ex.getHttpStatusCode()).body(errorResponse);
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
