package com.tmdb.api.search.adapter.rest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ApiExternalCall {

    private static final Logger logger = LoggerFactory.getLogger(ApiExternalCall.class);
    private final WebClient webClient;

    @Value("${tmdb.api.api-key}")
    private String apiKey;

    public ApiExternalCall(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Makes an HTTP GET call to an external API endpoint.
     * Utilizes a Circuit Breaker pattern to handle service failures gracefully.
     *
     * @param urlEndPoint  The API endpoint to call.
     * @param responseType The expected response type.
     * @param <T>          The generic type of the response.
     * @return A Mono wrapping the response object.
     */
    @CircuitBreaker(name = "tmdbApiCircuitBreaker", fallbackMethod = "fallbackGetMonoObject")
    public <T> Mono<T> getMonoObject(String urlEndPoint, Class<T> responseType) {
        return  webClient.get().uri(urlEndPoint)
                .headers(headers -> headers.setBearerAuth(apiKey))
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
                .bodyToMono(responseType)
                .doOnError(e -> logger.error("Error during API call: {}", e.getMessage()));
    }

    /**
     * Fallback method triggered by the Circuit Breaker in case of failures.
     *
     * @param urlEndPoint  The API endpoint that failed.
     * @param responseType The expected response type.
     * @param throwable    The cause of the fallback trigger.
     * @param <T>          The generic type of the response.
     * @return A Mono wrapping an error indicating service unavailability.
     */
    // Método de fallback
    <T> Mono<T> fallbackGetMonoObject(String urlEndPoint, Class<T> responseType, Throwable throwable) {
        logger.warn("Fallback triggered for endpoint: {}, due to: {}", urlEndPoint, throwable.getMessage());
        return Mono.error(new RuntimeException("Fallback: Service is temporarily unavailable."));
    }

}
