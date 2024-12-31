package com.tmdb.api.search.adapter.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmdb.api.search.exception.ApiExternalException;
import com.tmdb.api.search.model.ApiErrorItem;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ApiExternalCall {

    private static final Logger logger = LoggerFactory.getLogger(ApiExternalCall.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${tmdb.api.api-key}")
    private String apiKey;

    public ApiExternalCall(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
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
        return webClient.get()
                .uri(urlEndPoint)
                .headers(headers -> headers.setBearerAuth(apiKey))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            logger.error("Error during API call: Status {} Body: {}", response.statusCode().value(), body);

                            try {
                                ApiErrorItem errorResponse = objectMapper.readValue(body, ApiErrorItem.class);
                                logger.error("Detailed error: status_code={}, status_message={}",
                                        errorResponse.getStatus_code(),
                                        errorResponse.getStatus_message());

                                return Mono.error(new ApiExternalException(
                                        "Error during API call",
                                        errorResponse.isSuccess(),
                                        errorResponse.getStatus_message(), errorResponse.getStatus_code(),
                                        response.statusCode().value()));
                            } catch (JsonProcessingException e) {
                                logger.error("Error parsing error response body: {}", e.getMessage());
                                return Mono.error(new ApiExternalException(
                                        "Error during API call",
                                        false,
                                        "Error during API Call: Unable to parse error body",
                                        response.statusCode().value(),
                                        500
                                ));
                            }
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
