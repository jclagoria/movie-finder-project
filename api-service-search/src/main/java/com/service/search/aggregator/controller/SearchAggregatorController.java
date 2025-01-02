package com.service.search.aggregator.controller;

import com.service.search.aggregator.dto.SearchResponse;
import com.service.search.aggregator.service.MovieAggregatorServiceImpl;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/api/v1/searchs", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchAggregatorController {

    private final MovieAggregatorServiceImpl movieAggregatorService;

    public SearchAggregatorController(MovieAggregatorServiceImpl movieAggregatorService) {
        this.movieAggregatorService = movieAggregatorService;
    }

    @GetMapping
    public Mono<SearchResponse> getMoviesBeSearch(@RequestParam("query") String query,
                                                  @RequestParam(name = "includeAdult", defaultValue = "false", required = false) boolean includeAdult,
                                                  @RequestParam(name = "language", defaultValue = "en-US", required = false) String language,
                                                  @RequestParam(name = "primaryReleaseYear", required = false) String primaryReleaseYear,
                                                  @RequestParam(name = "page", defaultValue = "1", required = false) @Min(1) long page,
                                                  @RequestParam(name = "region", required = false) String region,
                                                  @RequestParam(name = "year", required = false) String year) {
        return movieAggregatorService.aggregateMovies(query, includeAdult, language,
                primaryReleaseYear, page, region, year);
    }

}
