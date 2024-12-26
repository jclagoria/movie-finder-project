package com.tmdb.api.search.controller;

import com.tmdb.api.search.dto.SearchItem;
import com.tmdb.api.search.dto.SearchRequest;
import com.tmdb.api.search.service.TMDBSearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(TMDBSearchMovieController.class)
public class TMDBSearchMovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TMDBSearchService tmdbSearchService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TMDBSearchService tmdbSearchService() {
            return Mockito.mock(TMDBSearchService.class); // Create and return a mock
        }
    }

    @Test
    void searchMovieByQuery_validInput_returnsOkResponse() {
        String query = "Inception";
        boolean includeAdult = false;
        String language = "en-US";
        String primaryReleaseYear = "2010";
        long page = 1;
        String region = "US";
        String year = "2010";

        SearchRequest mockResponse = getSearchRequest();
        when(tmdbSearchService.fetchSearchMovie(
                        anyString(), anyBoolean(), anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(Mono.just(mockResponse)); // Mock the service to return a valid Mono

        // Act and Assert: Perform the request and verify the response
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/search/movies")
                        .queryParam("query", query)
                        .queryParam("includeAdult", includeAdult)
                        .queryParam("language", language)
                        .queryParam("primaryReleaseYear", primaryReleaseYear)
                        .queryParam("page", page)
                        .queryParam("region", region)
                        .queryParam("year", year)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result[0].tmdbId").isNumber()
                .jsonPath("$.result[0].tmdbId").isEqualTo(763463)
                .jsonPath("$.result[0].title").isEqualTo("JONES")// Verify returned data
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.totalResults").isEqualTo(1);
    }

    private static SearchRequest getSearchRequest() {
        SearchItem movieItem = new SearchItem(763463,
                false,
                "/kYJLBFnADVnl5TWumKopZNGCrUf.jpg",
                List.of(18L), "en", "JONES",
                "Jones' inner demons threaten to take over her life unless she outruns the voices in her head and the ticking of time.",
                8.980, "/hNgCnxUw4FwK3wAFBDUyoDdZRQy.jpg",
                "2022-01-01", "JONES", false, 10);

        // Create mock response
        return new SearchRequest(List.of(movieItem), 1, 1, 1);
    }

    @Test
    void searchMovieByQuery_invalidInput_returnsBadRequest() {
        // Act and Assert: Perform the request with invalid input
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/search/movies")
                        .queryParam("query", "") // Invalid blank query
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest() // Expect HTTP 400 for validation errors
                .expectBody()
                .jsonPath("$.error").exists(); // Assuming validation errors are returned in a field named "errors"
    }

    @Test
    void searchMovieByQuery_serviceError_returnsServerError() {
        // Arrange: Mock service to throw an exception
        String query = "ErrorQuery";
        when(tmdbSearchService.fetchSearchMovie(
                        anyString(), anyBoolean(), anyString(),
                        anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        // Act and Assert: Perform the request and verify the response
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/search/movies")
                        .queryParam("query", query)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError(); // Expect HTTP 500 for service error
    }

}