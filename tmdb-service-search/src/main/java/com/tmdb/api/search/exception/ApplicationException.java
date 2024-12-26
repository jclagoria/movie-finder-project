package com.tmdb.api.search.exception;

public class ApplicationException extends RuntimeException {

    private long statusCode;

    public ApplicationException(String message, long statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
