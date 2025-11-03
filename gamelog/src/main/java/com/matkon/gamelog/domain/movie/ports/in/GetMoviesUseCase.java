package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.movie.model.Movie;
import org.springframework.data.domain.Page;

public interface GetMoviesUseCase {

    Page<Movie> getMovies(int page, int size);
}
