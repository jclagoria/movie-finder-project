package com.tmdb.api.search.exception;

public class ApiExternalException extends RuntimeException {

    private String titleMessage;
    private boolean success;
    private String error;
    private int statusCode;
    private int httpStatusCode;

    public ApiExternalException(String titleMessage, boolean success, String error, int statusCode, int httpStatusCode) {
        this.titleMessage = titleMessage;
        this.success = success;
        this.error = error;
        this.statusCode = statusCode;
        this.httpStatusCode = httpStatusCode;
    }

    public String getTitleMessage() {
        return titleMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
