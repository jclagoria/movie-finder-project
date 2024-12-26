package com.service.movies.service.mapper;

import com.service.movies.dto.MovieItemResponse;
import com.service.movies.model.Movie;

public class MovieMapper {

    /**
     * Maps a MovieItemResponse to a Movie entity.
     *
     * @param movieRequest The movie request to map.
     * @return The mapped Movie entity.
     */
    public static Movie mapToMovie(MovieItemResponse movieRequest) {
        Movie movie = new Movie();
        movie.setTmdbId(movieRequest.tmdbId());
        movie.setTitle(movieRequest.title());
        movie.setBackdropPath(movieRequest.backdropPath());
        movie.setGenreIds(movieRequest.genreIds());
        movie.setOriginalLanguage(movieRequest.originalLanguage());
        movie.setOriginalTitle(movieRequest.originalTitle());
        movie.setOverview(movieRequest.overview());
        movie.setPopularity(movieRequest.popularity());
        movie.setPosterPath(movieRequest.posterPath());
        movie.setReleaseDate(movieRequest.releaseDate());
        movie.setVideo(movieRequest.video());
        movie.setVoteAverage(movieRequest.voteAverage());
        movie.setAdult(movieRequest.adult());
        return movie;
    }

    /**
     * Maps a Movie entity to a MovieItemResponse.
     *
     * @param movie The Movie entity to map.
     * @return The mapped MovieItemResponse.
     */
    public static MovieItemResponse mapToMovieItemResponse(Movie movie) {
        return new MovieItemResponse(
                movie.getTmdbId(),
                movie.isAdult(),
                movie.getBackdropPath(),
                movie.getGenreIds(),
                movie.getOriginalLanguage(),
                movie.getOriginalTitle(),
                movie.getOverview(),
                movie.getPopularity(),
                movie.getPosterPath(),
                movie.getReleaseDate(),
                movie.getTitle(),
                movie.isVideo(),
                movie.getVoteAverage()
        );
    }

}
