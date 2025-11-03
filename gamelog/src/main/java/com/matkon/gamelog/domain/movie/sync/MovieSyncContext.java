package com.matkon.gamelog.domain.movie.sync;

public record MovieSyncContext(SyncType type, Long movieId) {

    public enum SyncType {
        SINGLE_MOVIE,
        ALL_MOVIES
    }

    public static MovieSyncContext singleMovie(Long movieId) {
        return new MovieSyncContext(SyncType.SINGLE_MOVIE, movieId);
    }

    public static MovieSyncContext allMovies() {
        return new MovieSyncContext(SyncType.ALL_MOVIES, null);
    }

}
