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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public Page<Movie> getMovies(int page, int size, String search) {
        return moviePersistencePort.getMovies(page, size, search);
    }

    @Override
    @Cacheable(value = "single_movie", key = "#id")
    public Movie getSingleMovie(Long id) {
        return moviePersistencePort.getSingleMovie(id);
    }

    @Override
    @Cacheable(value = "movies_search_results", key = "#query")
    public List<Movie> searchMovies(String query) {
        return movieInfoPort.searchMovies(query);
    }

    @Override
    @CacheEvict(value = "single_movie", allEntries = true)
    public void deleteMovie(Long movieId) {
        moviePersistencePort.deleteMovie(movieId);
    }

    @Override
    @CacheEvict(value = "single_movie", allEntries = true)
    public Movie saveMovie(Long tmdbId) {
        return moviePersistencePort.saveMovie(tmdbId);
    }

    @Override
    @CacheEvict(value = "single_movie", allEntries = true)
    public SyncResult syncSingleMovie(Long movieId) {
        return moviePersistencePort.syncSingleMovie(movieId);
    }

    @Override
    public SyncResult syncAllMovies() {
        return moviePersistencePort.syncAllMovies();
    }
}
