package com.tmdb.api.search.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SearchRequest (

        @NotBlank(message = "Query cannot be blank")
        String query,

        Boolean includeAdult,

        @Pattern(regexp = "^[a-z]{2}-[A-Z]{2}$", message = "Invalid language format. Use 'xx-XX'")
        String language,

        @Pattern(regexp = "^\\d{4}$", message = "Invalid year format. Use 'YYYY'")
        String primaryReleaseYear,

        @Min(value = 1, message = "Page must be at least 1")
        Long page,

        @Pattern(regexp = "^[A-Z]{2}$", message = "Region must be a 2-letter country code")
        String region,

        @Pattern(regexp = "^\\d{4}$", message = "Invalid year format. Use 'YYYY'")
        String year) {

    public SearchRequest {
        if (language == null || language.isBlank()) {
            language = "en-US";
        }

        if (includeAdult == null) {
            includeAdult = Boolean.FALSE;
        }

        if (page == null) {
            page = 1L;
        }
    }
}
