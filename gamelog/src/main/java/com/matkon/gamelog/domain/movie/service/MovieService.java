package com.matkon.gamelog.domain.movie.service;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.ports.in.DeleteMovieUseCase;
import com.matkon.gamelog.domain.movie.ports.in.GetMoviesUseCase;
import com.matkon.gamelog.domain.movie.ports.in.GetSingleMovieUseCase;
import com.matkon.gamelog.domain.movie.ports.in.SaveMovieUseCase;
import com.matkon.gamelog.domain.movie.ports.in.SearchMoviesUseCase;
import com.matkon.gamelog.domain.movie.ports.in.SyncAllMoviesUseCase;
import com.matkon.gamelog.domain.movie.ports.in.SyncSingleMovieUseCase;
import com.matkon.gamelog.domain.movie.ports.out.MovieInfoPort;
import com.matkon.gamelog.domain.movie.ports.out.MoviePersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieService implements GetMoviesUseCase, GetSingleMovieUseCase,
        SearchMoviesUseCase, DeleteMovieUseCase, SaveMovieUseCase, SyncSingleMovieUseCase, SyncAllMoviesUseCase {

    private MoviePersistencePort moviePersistencePort;
    private MovieInfoPort movieInfoPort;

    @Override
    public Page<Movie> getMovies(int page, int size) {
        return moviePersistencePort.getMovies(page, size);
    }

    @Override
    public Movie getSingleMovie(Long id) {
        return moviePersistencePort.getSingleMovie(id);
    }

    @Override
    public List<Movie> searchMovies(String query) {
        return movieInfoPort.searchMovies(query);
    }

    @Override
    public void deleteMovie(Long movieId) {
        moviePersistencePort.deleteMovie(movieId);
    }

    @Override
    public Movie saveMovie(Long tmdbId) {
        return moviePersistencePort.saveMovie(tmdbId);
    }

    @Override
    public SyncResult syncSingleMovie(Long movieId) {
        return moviePersistencePort.syncSingleMovie(movieId);
    }

    @Override
    public SyncResult syncAllMovies() {
        return moviePersistencePort.syncAllMovies();
    }
}
