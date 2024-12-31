package com.tmdb.api.search.dto;

public record ApiErrorResponse(String generalMessage, boolean success, String error, int statusCode, String time) {
}
