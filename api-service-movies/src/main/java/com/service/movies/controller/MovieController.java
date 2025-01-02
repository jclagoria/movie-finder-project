package com.service.movies.controller;

import com.service.movies.controller.validation.ValidQuery;
import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import com.service.movies.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@Validated
@Tag(name = "Movies API", description = "API for managing movies")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Search movies by title and adult content flag.
     *
     * @param query The title of the movie to search for.
     * @param includeAdult The flag to indicate if adult content is included.
     * @return A Flux stream of movies matching the search criteria.
     */
    @Operation(summary = "Search movies by title and adult content flag",
            description = "Fetch movies based on their title and whether they contain adult content.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MovieItemResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping
    public Flux<MovieItemResponse> searchMoviesByTitle(
            @Parameter(description = "The title of the movie to search for", required = true)
            @RequestParam @ValidQuery String query,
            @Parameter(description = "Whether to include adult content in the search results, for default is false")
            @RequestParam(name = "includeAdult", required = false, defaultValue = "false") boolean includeAdult) {
        logger.info("Searching for movies with title: '{}' and adult content: {}", query, includeAdult);
        return movieService.findMovieByTitleAndByIsAdultContent(query, includeAdult);
    }

    /**
     * Save a list of movies.
     *
     * @param movies The list of movies to save.
     * @return A Mono that emits a ResponseEntity with the result of the save operation.
     */
    @Operation(summary = "Save a list of movies",
            description = "Persist a list of movies into the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movies saved successfully",
                    content = @Content(schema = @Schema(implementation = ApiMovieResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiMovieResponse.class)))
    })
    @PostMapping
    public Mono<ResponseEntity<ApiMovieResponse>> saveMovies(
            @Parameter(description = "List of movies to save", required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MovieItemResponse.class))))
            @RequestBody @Valid List<@Valid MovieItemResponse> movies) {
        logger.info("Saving movies: {}", movies);
        return movieService.saveMovie(movies)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response)) // Return success response
                .onErrorResume(e -> {
                    logger.error("Error saving movies: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                });
    }

    /**
     * Delete a movie by its ID.
     *
     * @param id The ID of the movie to delete.
     * @return A Mono that emits a ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Delete a movie by its ID",
            description = "Delete a movie based on its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteMovie(
            @Parameter(description = "The ID of the movie to delete", required = true)
            @PathVariable @NotBlank(message = "Movie ID must not be blank") String id) {
        logger.info("Deleting movie with ID: {}", id);
        return movieService.deleteMovie(id)
                .thenReturn(ResponseEntity.ok().build())
                .onErrorResume(e -> {
                    logger.error("Error deleting movie with ID {}: {}", id, e.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    /**
     * Retrieve TMDB IDs by title and adult content flag.
     *
     * @param query The title of the movie to search for.
     * @param adult The flag to indicate if adult content is included.
     * @return A Flux stream of TMDB IDs matching the search criteria.
     */
    @Operation(summary = "Retrieve TMDB IDs by title and adult content flag",
            description = "Fetch TMDB IDs of movies based on their title and adult content flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "TMDB IDs found"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/search")
    public Flux<Long> getTmdbIdsByTitle(
            @Parameter(description = "The title of the movie to search for", required = true)
            @RequestParam @ValidQuery String query,
            @Parameter(description = "Whether to include adult content in the search results", required = true)
            @RequestParam boolean adult) {
        logger.info("Fetching TMDB IDs for title: '{}' and adult content: {}", query, adult);
        return movieService.findAllMoviesByQuery(query, adult);
    }

    /**
     * Find movies by their TMDB IDs.
     *
     * @param tmdbIds The TMDB IDs of the movies to find.
     * @return A Flux stream of TMDB IDs for the matching movies.
     */
    @Operation(summary = "Find movies by their TMDB IDs",
            description = "Retrieve movie details by providing their TMDB IDs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies found"),
            @ApiResponse(responseCode = "400", description = "Invalid TMDB IDs")
    })
    @GetMapping("/find-by-tmdb-ids")
    public Flux<Long>  findMoviesByTMDBId(
            @Parameter(description = "Comma-separated TMDB IDs of the movies", required = true)
            @RequestParam @NotBlank(message = "TMDB IDs must not be blank") String tmdbIds) {
        logger.info("Finding movies by TMDB IDs: {}", tmdbIds);
        return movieService.findAllMoviesByTMDBId(tmdbIds);
    }

}

