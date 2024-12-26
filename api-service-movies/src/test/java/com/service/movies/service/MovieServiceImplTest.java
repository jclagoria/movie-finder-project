package com.service.movies.service;

import com.service.movies.dto.ApiMovieResponse;
import com.service.movies.dto.MovieItemResponse;
import com.service.movies.model.Movie;
import com.service.movies.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;


@ActiveProfiles("test")
@DataMongoTest
@Import(MovieServiceImpl.class)
public class MovieServiceImplTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll().block();
    }

    @Test
    void testFindMovieByTitleAndByIsAdultContent() {
        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setAdult(false);

        movieRepository.save(movie).block();

        StepVerifier.create(movieService.findMovieByTitleAndByIsAdultContent("Inception", false))
                .expectNextMatches(result -> result.title().equals("Inception") && !result.adult())
                .verifyComplete();
    }

    @Test
    void testSaveMovie() {
        MovieItemResponse movieItem = new MovieItemResponse(
                12344L,
                false,
                "/path/to/backdrop1.jpg",
                List.of(28L, 12L, 16L),
                "en",
                "Original Movie Title 5",
                "This is the overview of the first movie",
                7.8,
                "/path/to/poster1.jpg",
                "2024-12-25",
                "Movie Title 5",
                false,
                8.5
        );

        Mono<ApiMovieResponse> result = movieService.saveMovie(List.of(movieItem));

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.code() == 201 && response.message().equals("Movies successfully saved."))
                .verifyComplete();

        StepVerifier.create(movieRepository.findByTmdbId(12344L))
                .expectNextMatches(movie ->
                        movie.getTitle().equals("Movie Title 5") &&
                                movie.getTmdbId() == 12344L &&
                                movie.getOriginalLanguage().equals("en") &&
                                movie.getVoteAverage() == 8.5)
                .verifyComplete();
    }

    @Test
    void testDeleteMovie() {
        Movie movie = new Movie();
        movie.setId("123");
        movie.setTitle("Inception");

        movieRepository.save(movie).block();

        StepVerifier.create(movieService.deleteMovie("123")).verifyComplete();

        StepVerifier.create(movieRepository.findById("123")).verifyComplete();
    }

    @Test
    void testFindAllMoviesByQuery() {
        Movie movie1 = new Movie();
        movie1.setTitle("Inception");
        movie1.setAdult(false);

        Movie movie2 = new Movie();
        movie2.setTitle("Inception");
        movie2.setAdult(false);

        movieRepository.saveAll(List.of(movie1, movie2)).blockLast();

        StepVerifier.create(movieService.findAllMoviesByQuery("Inception", false))
                .expectNextMatches(Objects::nonNull)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void testFindAllMoviesByTMDBId() {
        Movie movie1 = new Movie();
        movie1.setTmdbId(12345L);

        Movie movie2 = new Movie();
        movie2.setTmdbId(67890L);

        movieRepository.saveAll(List.of(movie1, movie2)).blockLast();

        StepVerifier.create(movieService.findAllMoviesByTMDBId("12345,67890"))
                .expectNext(12345L)
                .expectNext(67890L)
                .verifyComplete();
    }

}