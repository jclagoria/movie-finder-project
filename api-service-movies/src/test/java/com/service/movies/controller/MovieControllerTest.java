package com.service.movies.controller;

import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import com.service.movies.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MovieService movieService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public MovieService movieService() {
            return Mockito.mock(MovieService.class);
        }
    }

    @Test
    void searchMoviesByTitle_validInput_returnsOkResponse() {
        String query = "Inception";
        boolean includeAdult = false;

        MovieItemResponse mockMovie = new MovieItemResponse(1,
                false,
                null,
                List.of(23L, 57L),
                "en",
                "Original Movie title", "Movie overwiev",
                0.0, null,
                "2002-10-01", "Title Movie", false, 0);
        Flux<MovieItemResponse> mockResponse = Flux.just(mockMovie);

        when(movieService
                .findMovieByTitleAndByIsAdultContent(anyString(), anyBoolean())).thenReturn(mockResponse);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies")
                        .queryParam("query", query)
                        .queryParam("includeAdult", includeAdult)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").exists();
        verify(movieService, times(1))
                .findMovieByTitleAndByIsAdultContent(anyString(), anyBoolean());
    }

    @Test
    void saveMovies_validInput_returnsCreatedResponse() {
        ApiMovieResponse mockApiResponse = new ApiMovieResponse(201L, "Movies saved successfully");

        MovieItemResponse mockMovie = new MovieItemResponse(1,
                false,
                null,
                List.of(23L, 57L),
                "en",
                "Original Movie title", "Movie overwiev",
                0.0, null,
                "2002-10-01", "Title Movie", false, 0);

        List<MovieItemResponse> movies = List.of(mockMovie);
        when(movieService.saveMovie(any())).thenReturn(Mono.just(mockApiResponse));

        webTestClient.post()
                .uri("/api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(movies)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiMovieResponse.class)
                .value(response -> {
                    assertEquals(201L, response.code());
                    assertEquals("Movies saved successfully", response.message());
                });

        verify(movieService, times(1)).saveMovie(any());
    }

    @Test
    void deleteMovie_validId_returnsOkResponse() {
        String movieId = "12345";
        when(movieService.deleteMovie(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk();
        verify(movieService, times(1)).deleteMovie(anyString());
    }

    @Test
    void getTmdbIdsByTitle_validInput_returnsOkResponse() {
        String query = "Inception";
        boolean includeAdult = false;

        Flux<Long> mockResponse = Flux.just(12345L, 67890L);
        when(movieService.findAllMoviesByQuery(anyString(), anyBoolean())).thenReturn(mockResponse);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/search")
                        .queryParam("query", query)
                        .queryParam("includeAdult", includeAdult)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void findMoviesByTMDBId_validInput_returnsOkResponse() {
        String tmdbIds = "12345,67890";

        Flux<Long> mockResponse = Flux.just(12345L, 67890L);
        when(movieService.findAllMoviesByTMDBId(anyString())).thenReturn(mockResponse);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/find-by-tmdb-ids")
                        .queryParam("tmdbIds", tmdbIds)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0]").isNumber()
                .jsonPath("$[0]").isEqualTo(12345);
        verify(movieService, times(1)).findAllMoviesByTMDBId(anyString());
    }

}