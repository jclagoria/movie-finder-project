package com.tmdb.api.search.adapter.rest.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiExternalCallTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ApiExternalCall apiExternalCall;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMonoObject_success() {
        // Mock the WebClient behavior for a successful API call.
        String url = "https://api.example.com/data";
        String mockResponse = "{\"key\": \"value\"}";

        when(webClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);

        // Mock the onStatus behavior to return the same responseSpec
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> responseSpec);

        // Mock the bodyToMono method to return the mock response
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        // Invoke the method and verify the result.
        StepVerifier.create(apiExternalCall.getMonoObject(url, String.class))
                .expectNext(mockResponse)
                .verifyComplete();

        verify(webClient, times(1)).get();
    }

    @Test
    void testGetMonoObject_failure() {
        // Mock the WebClient behavior for a failed API call.
        String url = "https://api.example.com/data";
        String errorMessage = "Error occurred";

        when(webClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);

        // Mock the onStatus method to simulate an error response and return the responseSpec for further chaining
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        // Mock the bodyToMono to return a Mono.error to simulate an error response
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new WebClientResponseException(
                500, errorMessage, null, null, null)));

        // Invoke the method and verify the error is handled correctly.
        StepVerifier.create(apiExternalCall.getMonoObject(url, String.class))
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        throwable.getMessage().contains(errorMessage))
                .verify();

        verify(webClient, times(1)).get();
    }

    @Test
    void testFallbackGetMonoObject() {
        // Simulate the fallback behavior.
        String url = "https://api.example.com/data";
        Throwable cause = new RuntimeException("API call failed");

        StepVerifier.create(apiExternalCall.fallbackGetMonoObject(url, String.class, cause))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Fallback"))
                .verify();
    }


}