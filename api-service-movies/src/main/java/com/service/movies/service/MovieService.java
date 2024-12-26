package com.service.movies.service;

import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MovieService {
    Flux<MovieItemResponse> findMovieByTitleAndByIsAdultContent(String title, boolean isAdult);
    Mono<ApiMovieResponse> saveMovie(List<MovieItemResponse> movies);
    Mono<Void> deleteMovie(String id);
    Flux<Long> findAllMoviesByQuery(String title, boolean isAdult);
    Flux<Long> findAllMoviesByTMDBId(String moviesIds);
}
