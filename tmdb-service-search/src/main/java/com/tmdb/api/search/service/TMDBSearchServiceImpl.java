package com.tmdb.api.search.service;

import com.tmdb.api.search.adapter.rest.client.ApiExternalCall;
import com.tmdb.api.search.dto.SearchRequest;
import com.tmdb.api.search.mapper.SearchMapper;
import com.tmdb.api.search.model.SearchMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TMDBSearchServiceImpl implements TMDBSearchService {

    private static final Logger logger = LoggerFactory.getLogger(TMDBSearchServiceImpl.class);

    private final ApiExternalCall apiExternalCall;

    public TMDBSearchServiceImpl(ApiExternalCall apiExternalCall) {
        this.apiExternalCall = apiExternalCall;
    }

    @Override
    public Mono<SearchRequest> fetchSearchMovie(String query, boolean includeAdult, String language,
                                                String primaryReleaseYear, long page, String region, String year) {

        String urlSearch = UriComponentsBuilder.fromUriString("/search/movie")
                .queryParam("query", query)
                .queryParamIfPresent("include_adult",
                        includeAdult ? Optional.of(includeAdult) : Optional.empty())
                .queryParam("language", language != null ? language : "en-US")
                .queryParamIfPresent("primary_release_year", Optional.ofNullable(primaryReleaseYear))
                .queryParam("page", page > 0 ? page : 1 )
                .queryParamIfPresent("region", Optional.ofNullable(region))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .toUriString();

        // Logging para la trazabilidad
        logger.info("Fetching search movies with query: {}, includeAdult: {}, language: {}, " +
                        "primaryReleaseYear: {}, page: {}, region: {}, year: {}",
                query, includeAdult, language, primaryReleaseYear, page, region, year);
        logger.debug("Constructed URL for TMDB search: {}", urlSearch);

        return apiExternalCall.getMonoObject(urlSearch, SearchMovieResponse.class)
                .map(SearchMapper::mapToSearchRequest)
                .doOnNext(response -> logger.info("Successfully fetched movies for query: {}, Total Results: {}",
                        query, response.totalResults()))
                .onErrorResume(e -> {
                    logger.error("Fallback executed in fetchSearchMovie due to: {}", e.getMessage());
                    return Mono.empty();
                });
    }

}
