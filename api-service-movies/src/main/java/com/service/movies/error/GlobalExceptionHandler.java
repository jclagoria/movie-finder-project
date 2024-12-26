package com.service.movies.error;

import com.service.movies.exceptions.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation error");
        errorResponse.put("message", ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : "Invalid input");
        errorResponse.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Throwable ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        HttpStatus status;

        if (ex instanceof jakarta.validation.ConstraintViolationException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse.put("status", status.value());
            errorResponse.put("error", "Validation error");
            errorResponse.put("message", ex.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse.put("status", status.value());
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred. Please try again.");
        }

        errorResponse.put("timestamp", Instant.now());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessLogicExceptions(BusinessLogicException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", ex.getStatus());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", Instant.now());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

}
