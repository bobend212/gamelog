package com.matkon.gamelog.domain.movie.ports.out;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.movie.model.Movie;
import org.springframework.data.domain.Page;

public interface MoviePersistencePort {

    Page<Movie> getMovies(int page, int size, String search);

    Movie getSingleMovie(Long id);

    void deleteMovie(Long movieId);

    Movie saveMovie(Long tmdbId);

    SyncResult syncSingleMovie(Long movieId);

    SyncResult syncAllMovies();
}
