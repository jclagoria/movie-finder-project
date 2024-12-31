package com.tmdb.api.search.mapper;

import com.tmdb.api.search.dto.SearchItem;
import com.tmdb.api.search.dto.SearchResponse;
import com.tmdb.api.search.model.MovieItem;
import com.tmdb.api.search.model.SearchMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SearchMapper {

    private static final Logger logger = LoggerFactory.getLogger(SearchMapper.class);

    public static SearchResponse mapToSearchRequest(SearchMovieResponse response) {
        List<SearchItem> searchItems = response.getResults()
                .stream()
                .map(movieItem -> {
                    try {
                        return mapToSearchItem(movieItem);
                    } catch (Exception e) {
                        logger.error("Error mapping MovieItem to SearchItem: {}", movieItem, e);
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return new SearchResponse(
                searchItems,
                response.getPage(),
                response.getTotal_pages(),
                response.getTotal_results()
        );
    }

    private static SearchItem mapToSearchItem(MovieItem movieItem) {
        try {
            return new SearchItem(movieItem.getId(),
                    movieItem.isAdult(),
                    movieItem.getBackdrop_path(),
                    movieItem.getGenre_ids(),
                    movieItem.getOriginal_language(),
                    movieItem.getOriginal_title(),
                    movieItem.getOverview(),
                    movieItem.getPopularity(),
                    movieItem.getPoster_path(),
                    movieItem.getRelease_date(),
                    movieItem.getTitle(),
                    movieItem.isVideo(),
                    movieItem.getVote_average());
        } catch (Exception e) {
            logger.error("Error in transforming MovieItem to SearchItem: {}", movieItem, e);
            throw new RuntimeException("Error in transforming MovieItem to SearchItem", e);
        }
    }

}
