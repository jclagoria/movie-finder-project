package com.tmdb.api.search.adapter.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdb.api.base-url}")
    private String URL_API_TMDB;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(URL_API_TMDB)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    logger.info("Request: {} {}", request.method(), request.url());
                    return next.exchange(request)
                            .doOnSuccess(response -> logger.info("Response Status: {}",
                                    response.statusCode()));
                })
                .build();
    }


}
