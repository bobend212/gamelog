package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.movie.model.Movie;

import java.util.List;

public interface SearchMoviesUseCase {

    List<Movie> searchMovies(String query);
}
