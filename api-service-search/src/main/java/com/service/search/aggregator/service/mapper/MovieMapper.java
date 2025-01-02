package com.service.search.aggregator.service.mapper;

import com.service.search.aggregator.dto.Item;
import com.service.search.aggregator.model.MovieItem;

import java.util.Set;

public class MovieMapper {

    private MovieMapper() {
    }

    /**
     * Helper method to map a movie item to an enriched Item with saved flag.
     */
    public static Item mapToItemWithSavedFlag(MovieItem movieItem, Set<Long> localIdsSet) {
        boolean isSaved = localIdsSet.contains(movieItem.getTmdbId());
        return new Item(
                movieItem.getTmdbId(),
                movieItem.isAdult(),
                movieItem.getBackdropPath(),
                movieItem.getGenreIds(),
                movieItem.getOriginalLanguage(),
                movieItem.getOriginalTitle(),
                movieItem.getOverview(),
                movieItem.getPopularity(),
                movieItem.getPosterPath(),
                movieItem.getReleaseDate(),
                movieItem.getTitle(),
                movieItem.isVideo(),
                movieItem.getVoteAverage(),
                isSaved
        );
    }

}
