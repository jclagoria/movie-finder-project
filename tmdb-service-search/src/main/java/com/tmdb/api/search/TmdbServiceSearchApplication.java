package com.tmdb.api.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TmdbServiceSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmdbServiceSearchApplication.class, args);
	}

}
