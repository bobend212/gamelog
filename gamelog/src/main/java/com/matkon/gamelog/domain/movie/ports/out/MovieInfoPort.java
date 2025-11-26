package com.matkon.gamelog.domain.movie.ports.out;

import com.matkon.gamelog.domain.movie.model.Movie;

import java.time.LocalDate;
import java.util.List;

public interface MovieInfoPort {

    List<Movie> searchMovies(String query);

    Movie getSingleMovieDetails(Movie movie);

    Movie getSaveMovieDetails(Long tmdbId);

    LocalDate getReleaseDatePL(Long tmdbId);

    List<String> getMovieVodProviders(Long tmdbId);
}
