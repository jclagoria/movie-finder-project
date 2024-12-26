package com.tmdb.api.search.exception;

public class ApiExternalException extends RuntimeException {

    public long statusCode;

    public ApiExternalException(String message, Throwable cause, long statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
