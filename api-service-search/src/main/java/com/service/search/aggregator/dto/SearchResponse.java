package com.service.search.aggregator.dto;

import java.util.List;

public record SearchResponse(List<Item> result, long page,
                             long totalPages, long totalResults) {
}
