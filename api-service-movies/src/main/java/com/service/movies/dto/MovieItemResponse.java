package com.service.movies.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import javax.validation.constraints.Positive;
import java.util.List;

public record MovieItemResponse(
        @Positive(message = "TMDB ID must be a positive number") long tmdbId,
        boolean adult, String backdropPath,
        List<Long> genreIds,
        String originalLanguage,
        @NotBlank(message = "Original title must not be blank") String originalTitle,
        String overview,
        @PositiveOrZero(message = "Popularity must be zero or positive") double popularity,
        String posterPath,
        String releaseDate,
        String title,
        boolean video,
        @PositiveOrZero(message = "Popularity must be zero or positive") double voteAverage) {
}
