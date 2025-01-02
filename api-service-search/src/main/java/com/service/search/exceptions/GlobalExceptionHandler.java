package com.service.search.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Map<String, String>> handleBusinessLogicException
            (BusinessLogicException businessLogicException) {

        logger.error(businessLogicException.getMessage(), businessLogicException);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("MessageError", businessLogicException.getMessage());
        errorMap.put("timezone", Instant.now().toString());

        return ResponseEntity.status(businessLogicException.getCode()).body(errorMap);
    }

    @ExceptionHandler(SearchServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> handleSearchServiceException
            (SearchServiceException searchServiceException) {
        logger.error(searchServiceException.getMessage(), searchServiceException);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("MessageError", searchServiceException.getMessage());
        errorMap.put("timezone", Instant.now().toString());

        return Mono.just(errorMap);
    }

}
