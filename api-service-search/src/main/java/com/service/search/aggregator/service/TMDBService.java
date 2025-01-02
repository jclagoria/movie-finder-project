package com.service.search.aggregator.service;

import com.service.search.aggregator.model.SearchMovieResponse;
import reactor.core.publisher.Mono;

public interface TMDBService {

    Mono<SearchMovieResponse> fetchFromTMDB(String query, boolean includeAdult,
                                            String language, String primaryReleaseYear,
                                            long page, String region, String year);

}
