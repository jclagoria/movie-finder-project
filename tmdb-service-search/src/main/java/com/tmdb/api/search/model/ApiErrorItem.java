package com.tmdb.api.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiErrorItem {

    private boolean success;
    private String status_message;
    private int status_code;

    public ApiErrorItem() {
    }

    public ApiErrorItem(boolean success, String status_message, int status_code) {
        this.success = success;
        this.status_message = status_message;
        this.status_code = status_code;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
}
