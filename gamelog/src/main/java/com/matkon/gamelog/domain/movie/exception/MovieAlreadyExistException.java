package com.matkon.gamelog.domain.movie.exception;

public class MovieAlreadyExistException extends RuntimeException {
    public MovieAlreadyExistException(Long tmdbId) {
        super("Movie with TMDB ID " + tmdbId + " already exists in the database.");
    }
}
