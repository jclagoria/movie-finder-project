package com.service.search.aggregator.controller;

import com.service.search.aggregator.dto.SearchResponse;
import com.service.search.aggregator.service.MovieAggregatorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/api/v1/searchs", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchAggregatorController {

    private final MovieAggregatorService movieAggregatorService;

    public SearchAggregatorController(MovieAggregatorService movieAggregatorService) {
        this.movieAggregatorService = movieAggregatorService;
    }

    @GetMapping
    public Mono<SearchResponse> getMoviesBeSearch(String query) {
        return movieAggregatorService.aggregateMovies(query);
    }

}
