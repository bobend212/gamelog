package com.matkon.gamelog.domain.movie.ports.out;

import com.matkon.gamelog.domain.movie.model.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MovieInfoPort {

    List<Movie> searchMovies(String query);

    Movie getSingleMovieDetails(Movie movie);

    Movie getSaveMovieDetails(Long tmdbId);

    LocalDate getReleaseDatePL(Long tmdbId);

    Set<String> getMovieVodProviders(Long tmdbId);
}
