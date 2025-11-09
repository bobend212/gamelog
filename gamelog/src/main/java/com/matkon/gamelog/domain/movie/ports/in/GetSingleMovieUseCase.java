package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.movie.model.Movie;

public interface GetSingleMovieUseCase {

    Movie getSingleMovie(Long id);
}
