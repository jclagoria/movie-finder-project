package com.service.search.aggregator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchMovieResponse {

    @JsonProperty
    private List<MovieItem> result;

    @JsonProperty
    private long page;

    @JsonProperty
    private long totalPages;

    @JsonProperty
    private long totalResults;

    public List<MovieItem> getResult() {
        return result;
    }

    public void setResult(List<MovieItem> result) {
        this.result = result;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
}
