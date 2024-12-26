package com.tmdb.api.search.service;

import com.tmdb.api.search.adapter.rest.client.ApiExternalCall;
import com.tmdb.api.search.dto.SearchItem;
import com.tmdb.api.search.dto.SearchRequest;
import com.tmdb.api.search.mapper.SearchMapper;
import com.tmdb.api.search.model.MovieItem;
import com.tmdb.api.search.model.SearchMovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TMDBSearchServiceImplTest {

    private TMDBSearchServiceImpl service;

    @Mock
    private ApiExternalCall apiExternalCall;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TMDBSearchServiceImpl(apiExternalCall);

    }

    @Test
    void testFetchSearchMovie_Success() {
        // Arrange
        String query = "test";
        boolean includeAdult = false;
        String language = "en-US";
        String primaryReleaseYear = "2022";
        long page = 1;
        String region = "US";
        String year = "2022";

        SearchItem movieItem = new SearchItem(763463,
                false,
                "/kYJLBFnADVnl5TWumKopZNGCrUf.jpg",
                List.of(18L), "en", "JONES",
                "Jones' inner demons threaten to take over her life unless she outruns the voices in her head and the ticking of time.",
                8.980, "/hNgCnxUw4FwK3wAFBDUyoDdZRQy.jpg",
                "2022-01-01", "JONES", false, 10);

        MovieItem movieItem1 = new MovieItem();
        movieItem1.setId(763463);
        movieItem1.setAdult(false);
        movieItem1.setTitle("JONES");
        movieItem1.setPoster_path("/hNgCnxUw4FwK3wAFBDUyoDdZRQy.jpg");
        movieItem1.setBackdrop_path("/kYJLBFnADVnl5TWumKopZNGCrUf.jpg");
        movieItem1.setGenre_ids(List.of(18L));
        movieItem1.setVideo(false);
        movieItem1.setPopularity(8.980);
        movieItem1.setOverview("Jones inner demons threaten to take over her life unless she outruns the voices in her head and the ticking of time.");
        movieItem1.setOriginal_language("en");
        movieItem1.setOriginal_title("JONES");
        movieItem1.setRelease_date("2022-01-01");
        movieItem1.setVote_average(10);

        SearchMovieResponse mockResponse = new SearchMovieResponse();
        mockResponse.setPage(1);
        mockResponse.setTotal_pages(1);
        mockResponse.setTotal_results(1);
        mockResponse.setResults(List.of(movieItem1));

        SearchRequest expectedSearchRequest = new SearchRequest(List.of(movieItem), 1, 1, 1);

        // Mocking static mapper
        try (MockedStatic<SearchMapper> mockedStatic = mockStatic(SearchMapper.class)) {
            mockedStatic.when(() -> SearchMapper.mapToSearchRequest(mockResponse))
                    .thenReturn(expectedSearchRequest);

            // Capturando argumentos
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            when(apiExternalCall.getMonoObject(urlCaptor.capture(), eq(SearchMovieResponse.class)))
                    .thenReturn(Mono.just(mockResponse));

            // Act
            Mono<SearchRequest> result = service.fetchSearchMovie(query, includeAdult, language, primaryReleaseYear, page, region, year);

            // Assert
            StepVerifier.create(result)
                    .expectNext(expectedSearchRequest)
                    .verifyComplete();

            verify(apiExternalCall, times(1)).getMonoObject(anyString(), eq(SearchMovieResponse.class));

            // Validación de la URL generada
            String capturedUrl = urlCaptor.getValue();
            assertTrue(capturedUrl.contains("/search/movie"));
            assertTrue(capturedUrl.contains("query=test"));
            assertTrue(capturedUrl.contains("language=en-US"));
            assertTrue(capturedUrl.contains("primary_release_year=2022"));
            assertTrue(capturedUrl.contains("page=1"));
            assertTrue(capturedUrl.contains("region=US"));
            assertTrue(capturedUrl.contains("year=2022"));
        }
    }

    @Test
    void testFetchSearchMovie_ErrorHandling() {
        // Arrange
        String query = "test";
        boolean includeAdult = false;
        String language = "en-US";
        String primaryReleaseYear = "2022";
        long page = 1;
        String region = "US";
        String year = "2022";

        RuntimeException expectedException = new RuntimeException("API call failed");

        when(apiExternalCall.getMonoObject(anyString(), eq(SearchMovieResponse.class)))
                .thenReturn(Mono.error(expectedException));

        // Act
        Mono<SearchRequest> result = service.fetchSearchMovie(query, includeAdult, language, primaryReleaseYear, page, region, year);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0) // No elementos devueltos
                .verifyComplete(); // La llamada retorna vacío en caso de error

        verify(apiExternalCall, times(1)).getMonoObject(anyString(), eq(SearchMovieResponse.class));
    }

    @Test
    void testFetchSearchMovie_DefaultValues() {
        // Arrange
        String query = "test";
        boolean includeAdult = false;
        String language = null; // Debe usar "en-US" por defecto
        String primaryReleaseYear = null;
        long page = 0; // Debe usar 1 por defecto
        String region = null;
        String year = null;

        SearchMovieResponse mockResponse = new SearchMovieResponse();
        mockResponse.setPage(1);
        mockResponse.setTotal_pages(1);
        mockResponse.setTotal_results(1);
        mockResponse.setResults(List.of());

        SearchRequest expectedSearchRequest = new SearchRequest(List.of(), 1, 1, 1);

        // Mocking static mapper
        try (MockedStatic<SearchMapper> mockedStatic = mockStatic(SearchMapper.class)) {
            mockedStatic.when(() -> SearchMapper.mapToSearchRequest(mockResponse))
                    .thenReturn(expectedSearchRequest);

            // Capturando argumentos
            ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
            when(apiExternalCall.getMonoObject(urlCaptor.capture(), eq(SearchMovieResponse.class)))
                    .thenReturn(Mono.just(mockResponse));

            // Act
            Mono<SearchRequest> result = service.fetchSearchMovie(query, includeAdult, language, primaryReleaseYear, page, region, year);

            // Assert
            StepVerifier.create(result)
                    .expectNext(expectedSearchRequest)
                    .verifyComplete();

            verify(apiExternalCall, times(1)).getMonoObject(anyString(), eq(SearchMovieResponse.class));

            // Validación de la URL generada con valores predeterminados
            String capturedUrl = urlCaptor.getValue();
            assertTrue(capturedUrl.contains("/search/movie"));
            assertTrue(capturedUrl.contains("query=test"));
            assertTrue(capturedUrl.contains("language=en-US")); // Valor por defecto
            assertTrue(capturedUrl.contains("page=1")); // Valor por defecto
            assertFalse(capturedUrl.contains("primary_release_year")); // No debe estar presente
            assertFalse(capturedUrl.contains("region")); // No debe estar presente
            assertFalse(capturedUrl.contains("year")); // No debe estar presente
        }
    }

}