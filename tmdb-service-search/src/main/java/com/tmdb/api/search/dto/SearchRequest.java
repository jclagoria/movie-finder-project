package com.tmdb.api.search.dto;

import java.util.List;

public record SearchRequest(List<SearchItem> result, long page,
                            long totalPages, long totalResults) {
}
