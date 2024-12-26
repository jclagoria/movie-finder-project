package com.service.movies.service;

import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import com.service.movies.exceptions.BusinessLogicException;
import com.service.movies.model.Movie;
import com.service.movies.model.TmdbIdProjection;
import com.service.movies.repository.MovieRepository;
import com.service.movies.service.mapper.MovieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
                .doOnError(e -> logger.error("Error fetching movies by title and adult content filter", e));
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
                .toList();

        logger.info("Saving movies with TMDb IDs: {}", tmdbIds);

        // Check if the movie already exists by tmdbId
        return movieRepository.findByTmdbIdIn(tmdbIds)
                .collectList()
                .flatMap(existingMovies -> {
                    List<Long> existingIds = existingMovies.stream()
                            .map(Movie::getTmdbId)
                            .toList();

                    if (new HashSet<>(existingIds).containsAll(tmdbIds)) {
                        String errorMsg = "All movies already exist with TMDb IDs: " + existingIds;
                        logger.warn(errorMsg);
                        return Mono.error(new BusinessLogicException(errorMsg, HttpStatus.BAD_REQUEST));
                    }

                    // Filter movies that are not in the database
                    List<MovieItemResponse> moviesToSave = movies.stream()
                            .filter(movie -> !existingIds.contains(movie.tmdbId()))
                            .toList();

                    // Save the filtered movies
                    return Flux.fromIterable(moviesToSave)
                            .flatMap(movieRequest -> {
                                Movie movie = MovieMapper.mapToMovie(movieRequest);
                                logger.info("Saving movie: {}", movie.getTitle());
                                return movieRepository.save(movie)
                                        .doOnSuccess(saved -> logger.info("Saved movie: {}", saved.getTitle()));
                            }).then(Mono.defer(() -> {
                                ApiMovieResponse response =
                                        new ApiMovieResponse(201, "Movies successfully saved.");
                                logger.info(response.message());
                                return Mono.just(response);
                            }))
                                .doOnError(e -> logger.error("Error saving movies", e));

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
        return movieRepository.deleteById(id)
                .doOnSuccess(unused -> logger.info("Deleted movie with ID: {}", id))
                .doOnError(e -> logger.error("Error deleting movie with ID: {}", id, e));
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
                .doOnError(e -> logger.error("Error fetching TMDb IDs by query", e));
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
                .doOnError(e -> logger.error("Error fetching TMDb IDs", e));
    }
}
