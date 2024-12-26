package com.tmdb.api.search.dto;

import java.util.List;

public record SearchItem(long tmdbId, boolean adult, String backdropPath,
                         List<Long> genreIds, String originalLanguage, String originalTitle,
                         String overview, double popularity, String posterPath, String releaseDate,
                         String title, boolean video, double voteAverage) {

}
