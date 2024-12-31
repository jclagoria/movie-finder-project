package com.tmdb.api.search.controller;

import com.tmdb.api.search.controller.validation.ValidLanguage;
import com.tmdb.api.search.controller.validation.ValidPage;
import com.tmdb.api.search.controller.validation.ValidRegion;
import com.tmdb.api.search.controller.validation.ValidYear;
import com.tmdb.api.search.dto.SearchRequest;
import com.tmdb.api.search.dto.SearchResponse;
import com.tmdb.api.search.service.TMDBSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TMDBSearchMovieController {

    private final Logger logger = LoggerFactory.getLogger(TMDBSearchMovieController.class);

    private final TMDBSearchService tmdbSearchService;

    public TMDBSearchMovieController(TMDBSearchService tmdbSearchService) {
        this.tmdbSearchService = tmdbSearchService;
    }

    @Operation(summary = "Search movies by query",
            description = "Fetch movies from TMDB based on search query parameters.",
            responses = {
                    @ApiResponse(description = "Successful Response", responseCode = "200",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SearchRequest.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400")
            })
    @GetMapping("/movies")
    public Mono<ResponseEntity<SearchResponse>> searchMovieByQuery(
            @RequestParam(name ="query") String query,
            @RequestParam(name = "includeAdult", defaultValue = "false") boolean includeAdult,
            @RequestParam(name = "language", defaultValue = "en-US") @ValidLanguage String language,
            @RequestParam(value = "primaryReleaseYear", required = false) @ValidYear String primaryReleaseYear,
            @RequestParam(name = "page", defaultValue = "1") @ValidPage long page,
            @RequestParam(name = "region", required = false) @ValidRegion String region,
            @RequestParam(name = "year", required = false) @ValidYear String year) {

        logger.info("Enpoint /movies called");

        return tmdbSearchService.fetchSearchMovie(query, includeAdult, language,
                        primaryReleaseYear, page, region, year)
                .map(ResponseEntity::ok)
                .doOnError(e -> logger.error("Error fetching movies: {}", e.getMessage()));
    }

}
