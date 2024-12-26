package com.service.search.aggregator.service;

import com.service.search.aggregator.dto.Item;
import com.service.search.aggregator.dto.SearchResponse;
import com.service.search.aggregator.model.SearchMovieResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieAggregatorService {

    private final WebClient webClient;

    public MovieAggregatorService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<SearchResponse> aggregateMovies(String query) {

        Flux<Long> localMovieIds = fetchLocalMovieIds(query);
        Mono<SearchMovieResponse> tmdbMoviesResponse = fetchFromTMDB(query);

        return tmdbMoviesResponse.flatMapMany(tmdbResponse ->
                localMovieIds
                        .collectList()
                        .flatMapMany(localIds -> Flux.fromIterable(tmdbResponse.getResult())
                                .map(movieItem -> {
                                    boolean isSaved = localIds.contains(movieItem.getTmdbId());
                                    return new Item(
                                            movieItem.getTmdbId(),
                                            movieItem.isAdult(),
                                            movieItem.getBackdropPath(),
                                            movieItem.getGenreIds(),
                                            movieItem.getOriginalLanguage(),
                                            movieItem.getOriginalTitle(),
                                            movieItem.getOverview(),
                                            movieItem.getPopularity(),
                                            movieItem.getPosterPath(),
                                            movieItem.getReleaseDate(),
                                            movieItem.getTitle(),
                                            movieItem.isVideo(),
                                            movieItem.getVoteAverage(),
                                            isSaved
                                    );
                                })
                        ).collectList()
                        .map(items -> new SearchResponse(
                                items,
                                tmdbResponse.getPage(),
                                tmdbResponse.getTotalPages(),
                                tmdbResponse.getTotalResults()
                        ))
        ).single();
    }

    private Flux<Long> fetchLocalMovieIds(String query) {
        return webClient.get()
                .uri("")
                .retrieve()
                .bodyToFlux(Long.class);
    }

    private Mono<SearchMovieResponse> fetchFromTMDB(String query) {
        return webClient.get()
                .uri("")
                .retrieve()
                .bodyToMono(SearchMovieResponse.class);
    }

}
