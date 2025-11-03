package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.movie.model.Movie;

public interface SaveMovieUseCase {

    Movie saveMovie(Long tmdbId);
}
