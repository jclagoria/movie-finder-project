package com.tmdb.api.search.service;

import com.tmdb.api.search.dto.SearchResponse;
import reactor.core.publisher.Mono;

public interface TMDBSearchService {

    /**
     * Fetches a list of movies from the TMDB API based on search criteria.
     * The result is cached using Spring's caching mechanism with a composite key.
     *
     * @param query               The search query for the movie title
     * @param includeAdult        Flag to include adult content in search results
     * @param language            Preferred language for search results (default: "en-US")
     * @param primaryReleaseYear  Specific year for the primary release of the movie
     * @param page                Page number for paginated results (default: 1)
     * @param region              Region to filter results (e.g., "US")
     * @param year                General year filter for the movies
     * @return Mono<SearchRequest> Reactive wrapper containing search results or empty if an error occurs
     */
    Mono<SearchResponse> fetchSearchMovie(String query, boolean includeAdult, String language,
                                          String primaryReleaseYear, long page, String region, String year);

}
