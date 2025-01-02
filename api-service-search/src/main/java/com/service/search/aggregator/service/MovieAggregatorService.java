package com.service.search.aggregator.service;

import com.service.search.aggregator.dto.SearchResponse;
import reactor.core.publisher.Mono;

public interface MovieAggregatorService {

    Mono<SearchResponse> aggregateMovies(String query, boolean includeAdult,
                                                String language, String primaryReleaseYear,
                                                long page, String region, String year);

}
