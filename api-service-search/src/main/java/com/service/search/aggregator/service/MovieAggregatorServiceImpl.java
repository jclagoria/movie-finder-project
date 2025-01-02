package com.service.search.aggregator.service;

import com.service.search.aggregator.dto.SearchResponse;
import com.service.search.aggregator.model.SearchMovieResponse;
import com.service.search.aggregator.service.mapper.MovieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieAggregatorServiceImpl implements MovieAggregatorService {

    private final Logger logger = LoggerFactory.getLogger(MovieAggregatorServiceImpl.class);

    private final LocalMovieService localMovieService;
    private final TMDBService tmdbService;

    public MovieAggregatorServiceImpl(LocalMovieService localMovieService, TMDBService tmdbService) {
        this.localMovieService = localMovieService;
        this.tmdbService = tmdbService;
    }

    @Override
    public Mono<SearchResponse> aggregateMovies(String query, boolean includeAdult,
                                                String language, String primaryReleaseYear,
                                                long page, String region, String year) {

        Flux<Long> localMovieIds = localMovieService.fetchLocalMovieIds(query, includeAdult)
                .switchIfEmpty(Flux.defer(() -> {
                    logger.info("No local movie IDs found for query: {}", query);
                    return Flux.empty();
                }));

        Mono<SearchMovieResponse> tmdbMoviesResponse = tmdbService.
                fetchFromTMDB(query, includeAdult, language,
                        primaryReleaseYear, page, region, year)
                .onErrorResume(throwable -> {
                    /**
                     * todo change an type or error
                     */
                    logger.error("Error fetching movies from TMDB", throwable);
                    return Mono
                            .just(new SearchMovieResponse(Collections.emptyList(), 0, 0, 0));
                });;

        return tmdbMoviesResponse.flatMapMany(tmdbResponse -> localMovieIds.collect(Collectors.toSet())
                .flatMapMany(localIdsSet -> Flux.fromIterable(
                        Optional.ofNullable(tmdbResponse.getResult()).orElse(Collections.emptyList())
                ).map(movieItem -> {
                    try {
                        return MovieMapper.mapToItemWithSavedFlag(movieItem, localIdsSet);
                    } catch (Exception e) {
                        logger.error("Error mapping movie item: {}", movieItem, e);
                        return null; // Or handle accordingly
                    }
                }))
                .filter(Objects::nonNull) // Remove nulls caused by errors
                .collectList()
                .map(enrichedItems -> new SearchResponse(
                        enrichedItems,
                        tmdbResponse.getPage(),
                        tmdbResponse.getTotalPages(),
                        tmdbResponse.getTotalResults()
                ))).single();
    }

}
