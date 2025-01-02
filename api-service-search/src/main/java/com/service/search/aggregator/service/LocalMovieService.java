package com.service.search.aggregator.service;

import reactor.core.publisher.Flux;

public interface LocalMovieService {

    Flux<Long> fetchLocalMovieIds(String query, boolean includeAdult);
}
