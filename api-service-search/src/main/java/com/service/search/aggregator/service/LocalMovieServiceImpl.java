package com.service.search.aggregator.service;

import com.service.search.exceptions.BusinessLogicException;
import com.service.search.exceptions.SearchServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class LocalMovieServiceImpl implements LocalMovieService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${local.service.url}")
    private String LOCAL_SERVICE_URL;

    private final WebClient webClient;

    public LocalMovieServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<Long> fetchLocalMovieIds(String query, boolean includeAdult) {
        String urlSearch = UriComponentsBuilder.fromUriString(LOCAL_SERVICE_URL)
                .queryParam("query", query)
                .queryParam("includeAdult", includeAdult).toUriString();

        logger.info("Fetching movies from Local Movies: {}", urlSearch);

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
                .bodyToFlux(Long.class).onErrorResume(throwable -> {
                    logger.error("Error to fetch local movies ", throwable);
                    return Flux.empty();
                });
    }

    private Mono handleErrorResponse(ClientResponse response) {
        return response.bodyToMono(Map.class)
                .flatMap(errorMessage -> {
                    HttpStatus status = mapToHttpStatus(response.statusCode());
                    return handleResponseError(errorMessage, status);
                });
    }

    private HttpStatus mapToHttpStatus(HttpStatusCode statusCode) {
        if (statusCode instanceof HttpStatus) {
            return (HttpStatus) statusCode;
        }
        // Fallback handling in case it's not an HttpStatus instance
        return HttpStatus.valueOf(statusCode.value());
    }

    private Mono<SearchServiceException> handleResponseError(Map<String, Object> errorMessage,
                                                              HttpStatus status) {
        String errorBody = (String) errorMessage.getOrDefault("error", "Unknown error");
        return Mono.error(new SearchServiceException(errorBody, status.value()));
    }

}
