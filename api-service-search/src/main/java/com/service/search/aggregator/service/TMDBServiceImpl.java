package com.service.search.aggregator.service;

import com.service.search.aggregator.model.SearchMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TMDBServiceImpl implements TMDBService {

    private final Logger logger = LoggerFactory.getLogger(TMDBServiceImpl.class);

    @Value("${movie.service.url}")
    private String MOVIE_SERVICE_URL;

    private final WebClient webClient;

    public TMDBServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<SearchMovieResponse> fetchFromTMDB(String query, boolean includeAdult,
                                                   String language, String primaryReleaseYear,
                                                   long page, String region, String year) {
        String urlSearch = UriComponentsBuilder.fromUriString(MOVIE_SERVICE_URL)
                .queryParam("query", query)
                .queryParam("includeAdult", includeAdult)
                .queryParam("language", language != null ? language : "en-US")
                .queryParamIfPresent("primaryReleaseYear", Optional.ofNullable(primaryReleaseYear))
                .queryParam("page", page > 0 ? page : 1 )
                .queryParamIfPresent("region", Optional.ofNullable(region))
                .queryParamIfPresent("year", Optional.ofNullable(year))
                .toUriString();

        logger.info("Fetching movies from TMDB: {}", query);

        return webClient.get()
                .uri(urlSearch)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            logger.error("Error during API call: Status {} Body: {}", response.statusCode(), body);
                            return Mono.error(new WebClientResponseException(
                                    "Error during API Call: " + body,
                                    response.statusCode().value(),
                                    response.headers().asHttpHeaders().toString(),
                                    null, null, null));
                        }))
                .bodyToMono(SearchMovieResponse.class)

                .onErrorResume(e -> {
                    logger.error("Failed to fetch from TMDB", e);
                    return Mono.empty();
                });
    }
}
