package com.service.search.exceptions;

public class SearchServiceException extends RuntimeException {

    private long code;

    public SearchServiceException(String message, long code) {
        super(message);
        this.code = code;
    }

    public long getCode() {
        return code;
    }
}
