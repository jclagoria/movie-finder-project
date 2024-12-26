package com.service.movies.repository;

import com.service.movies.model.Movie;
import com.service.movies.model.TmdbIdProjection;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
    Mono<Movie> findByTmdbId(long tmdbId);
    @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'adult': ?1 }")
    Flux<Movie> findByTitleContainingIgnoreCaseAndAdult(String title, boolean adult);
    @Query("{ 'tmdbId': { $in: ?0 } }")
    Flux<Movie> findByTmdbIdIn(List<Long> tmdbIds);
    @Query(value = "{ 'tmdbId': { $in: ?0 } }", fields = "{ 'tmdbId': 1 }")
    Flux<TmdbIdProjection> findTmdbIdsByTmdbIdIn(List<Long> tmdbIds);
}
