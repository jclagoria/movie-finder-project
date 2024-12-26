package com.service.movies.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "movies")
public class Movie {

    @Id
    private String id;
    private long tmdbId;
    private String imdbId;
    private String title;
    private String backdropPath;
    private List<Long> genreIds;
    private String originalLanguage;
    private String originalTitle;
    private String overview;
    private double popularity;
    private String posterPath;
    private String releaseDate;
    private boolean video;
    private double voteAverage;
    private long voteCount;
    private boolean adult;

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public Movie() {
    }

    public Movie(String id, long tmdbId, String imdbId, String title, String backdropPath,
                 List<Long> genreIds, String originalLanguage, String originalTitle, String overview,
                 double popularity, String posterPath, String releaseDate,
                 boolean video, double voteAverage, long voteCount, boolean adult) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.imdbId = imdbId;
        this.title = title;
        this.backdropPath = backdropPath;
        this.genreIds = genreIds;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.video = video;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.adult = adult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return tmdbId == movie.tmdbId && Double.compare(popularity, movie.popularity) == 0
                && video == movie.video && Double.compare(voteAverage, movie.voteAverage) == 0
                && voteCount == movie.voteCount && adult == movie.adult && Objects.equals(id, movie.id)
                && Objects.equals(imdbId, movie.imdbId) && Objects.equals(title, movie.title)
                && Objects.equals(backdropPath, movie.backdropPath) && Objects.equals(originalLanguage, movie.originalLanguage)
                && Objects.equals(originalTitle, movie.originalTitle) && Objects.equals(overview, movie.overview)
                && Objects.equals(posterPath, movie.posterPath) && Objects.equals(releaseDate, movie.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tmdbId, imdbId, title, backdropPath, originalLanguage, originalTitle,
                overview, popularity, posterPath, releaseDate, video, voteAverage, voteCount, adult);
    }
}
