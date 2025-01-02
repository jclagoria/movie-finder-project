package com.service.movies.service;

import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import com.service.movies.exceptions.ApplicationException;
import com.service.movies.exceptions.BusinessLogicException;
import com.service.movies.model.Movie;
import com.service.movies.model.TmdbIdProjection;
import com.service.movies.repository.MovieRepository;
import com.service.movies.service.mapper.MovieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Finds movies by title and adult content filter and maps to MovieItemResponse.
     *
     * @param title   The title (or partial title) of the movie.
     * @param isAdult Boolean to filter adult content.
     * @return Flux<MovieItemResponse> Reactive stream of mapped movie responses.
     */
    @Override
    public Flux<MovieItemResponse> findMovieByTitleAndByIsAdultContent(String title, boolean isAdult) {
        logger.info("Fetching movies with title containing '{}' and isAdult = {}", title, isAdult);
        return movieRepository.findByTitleContainingIgnoreCaseAndAdult(title, isAdult)
                .map(MovieMapper::mapToMovieItemResponse)
                .doOnError(e -> logger.error("Error fetching movies by title and adult content filter", e))
                .onErrorResume(e -> {
                   logger.error("Providing a fallback response of error: {}", e.getMessage());
                   return Flux.error(new ApplicationException("An error occurred when try to find movie by title and is an adult content"));
                });
    }

    /**
     * Saves a list of movies if they don't already exist in the database.
     *
     * @param movies List of movies to save.
     * @return Mono<ApiMovieResponse> Reactive response with HTTP status and ApiResponse.
     */
    @Override
    public Mono<ApiMovieResponse> saveMovie(List<MovieItemResponse> movies) {
        List<Long> tmdbIds = movies.stream()
                .map(MovieItemResponse::tmdbId)
                .filter(Objects::nonNull)
                .toList();

        logger.info("Saving movies with TMDb IDs: {}", tmdbIds);

        // Check if the movie already exists by tmdbId
        return movieRepository.findByTmdbIdIn(tmdbIds)
                .collectList()
                .flatMap(existingMovies -> {
                    Set<Long> existingIds = existingMovies.stream()
                            .map(Movie::getTmdbId)
                            .collect(Collectors.toSet());

                    List<Movie> moviesToSave = movies.stream()
                            .filter(movie -> !existingIds.contains(movie.tmdbId()))
                            .map(MovieMapper::mapToMovie)
                            .toList();

                    if (moviesToSave.isEmpty()) {
                        String errorMsg = "All movies already exist with TMDb IDs: " + existingIds;
                        logger.warn(errorMsg);
                        return Mono.error(new BusinessLogicException(errorMsg, HttpStatus.BAD_REQUEST));
                    }

                    logger.info("Movies to save: {}", moviesToSave.stream()
                            .map(Movie::getTitle)
                            .toList());

                    // Save movies one by one using Flux
                    return Flux.fromIterable(moviesToSave)
                            .flatMap(movieRepository::save) // Save each movie individually
                            .doOnNext(saved -> logger.info("Saved movie: {}", saved.getTitle()))
                            .then(Mono.just(new ApiMovieResponse(HttpStatus.OK.value(), "Movies successfully saved.")));
                })
                .onErrorResume(e -> {
                    logger.error("Database connection error: {}", e.getMessage());
                    return Mono.error(new ApplicationException("Failed to save movies due to a database error."));
                });
    }

    /**
     * Deletes a movie by its ID.
     *
     * @param id The ID of the movie to delete.
     * @return Mono<Void> Reactive completion signal.
     */
    @Override
    public Mono<Void> deleteMovie(String id) {
        logger.info("Deleting movie with ID: {}", id);
        return movieRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessLogicException("Movie Not Found with ID: " + id, HttpStatus.NOT_FOUND)))
                .flatMap(existingMovie -> movieRepository.deleteById(id))
                .doOnSuccess(unused -> logger.info("Deleted movie with ID: {}", id))
                .doOnError(e -> logger.error("Error deleting movie with ID: {}", id, e))
                .onErrorResume(e -> {
                    logger.error("Found Error with the data base: {}", e.getMessage());
                    return Mono.error(new ApplicationException("Found Error with the data base"));
                });
    }

    /**
     * Finds all movies by title and adult content filter and returns their TMDb IDs.
     *
     * @param title   The title (or partial title) of the movie.
     * @param isAdult Boolean to filter adult content.
     * @return Flux<Long> Reactive stream of TMDb IDs.
     */
    @Override
    public Flux<Long> findAllMoviesByQuery(String title, boolean isAdult) {
        logger.info("Fetching TMDb IDs for movies with title containing '{}' and isAdult = {}", title, isAdult);
        return movieRepository.findByTitleContainingIgnoreCaseAndAdult(title, isAdult)
                .map(Movie::getTmdbId)
                .doOnError(e -> logger.error("Error fetching TMDb IDs by query", e))
                .onErrorResume(e -> {
                    logger.error("Providing fallback for TMDb IDs query error: {}", e.getMessage());
                    return Flux.error(new ApplicationException("An error occurred when try to find all movies by query"));
                });
    }


    /**
     * Finds TMDb IDs for a comma-separated list of TMDb IDs.
     *
     * @param moviesId Comma-separated list of TMDb IDs.
     * @return Flux<Long> Reactive stream of TMDb IDs.
     */
    @Override
    public Flux<Long> findAllMoviesByTMDBId(String moviesId) {
        List<Long> tmdbIdList = Arrays.stream(moviesId.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .toList();

        logger.info("Fetching TMDb IDs for provided list: {}", tmdbIdList);
        return movieRepository.findTmdbIdsByTmdbIdIn(tmdbIdList)
                .map(TmdbIdProjection::getTmdbId)
                .doOnError(e -> logger.error("Error fetching TMDb IDs", e))
                .onErrorResume(e -> {
                    logger.error("Fallback: No TMDb IDs found for the provided list due to error: {}", e.getMessage());
                    return Flux.error(new ApplicationException("An error occurred when try to Fetching TMDb IDs for provided list"));
                });
    }
}
