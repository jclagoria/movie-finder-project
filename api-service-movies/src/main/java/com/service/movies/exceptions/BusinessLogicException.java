package com.service.movies.exceptions;

import jdk.jshell.Snippet;
import org.springframework.http.HttpStatus;

public class BusinessLogicException extends RuntimeException {

    private HttpStatus status;

    public BusinessLogicException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
