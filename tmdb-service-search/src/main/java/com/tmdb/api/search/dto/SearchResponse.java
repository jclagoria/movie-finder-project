package com.tmdb.api.search.dto;

import java.util.List;

public record SearchResponse(List<SearchItem> result, long page,
                             long totalPages, long totalResults) {
}
