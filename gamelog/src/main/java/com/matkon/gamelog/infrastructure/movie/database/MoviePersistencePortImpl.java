package com.matkon.gamelog.infrastructure.movie.database;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.movie.exception.MovieAlreadyExistException;
import com.matkon.gamelog.domain.movie.exception.MovieNotFoundException;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.ports.out.MovieInfoPort;
import com.matkon.gamelog.domain.movie.ports.out.MoviePersistencePort;
import com.matkon.gamelog.domain.movie.sync.MovieSyncContext;
import com.matkon.gamelog.domain.movie.sync.MovieSyncStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class MoviePersistencePortImpl implements MoviePersistencePort {

    private final MovieJpaRepository movieJpaRepository;
    private final MovieMapper movieMapper;
    private final MovieInfoPort movieInfoPort;
    private final List<MovieSyncStrategy> syncStrategies;

    @Override
    public Page<Movie> getMovies(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MovieEntity> movies = movieJpaRepository.findMovies(pageable, search);
        return movies.map(movieMapper::mapMovieEntityToMovie);
    }

    @Override
    public Movie getSingleMovie(Long id) {
        Optional<MovieEntity> movieOpt = movieJpaRepository.findById(id);
        MovieEntity movieEntity = movieOpt.orElseThrow(() ->
                new MovieNotFoundException("Movie with ID '%s' not found in the database".formatted(id)));

        Movie movie = movieMapper.mapMovieEntityToMovie(movieEntity);
        movie.setReleaseDatePL(movieInfoPort.getReleaseDatePL(movie.getTmdbId()));

        return movieInfoPort.getSingleMovieDetails(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        if (!movieJpaRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie with ID '%s' not found in the database".formatted(movieId));
        }
        movieJpaRepository.deleteById(movieId);
    }

    @Override
    public Movie saveMovie(Long tmdbId) {
        movieJpaRepository.findByTmdbId(tmdbId)
                .ifPresent(movie -> {
                    throw new MovieAlreadyExistException(tmdbId);
                });

        Movie movie = Optional.ofNullable(movieInfoPort.getSaveMovieDetails(tmdbId))
                .orElseThrow(() -> new MovieNotFoundException(
                        "Movie with ID '%s' not found in external API".formatted(tmdbId)));

        movie.setVodProviders(movieInfoPort.getVodProviders(tmdbId));

        MovieEntity savedMovie = movieJpaRepository.save(movieMapper.mapMovieToMovieEntity(movie));

        return movieMapper.mapMovieEntityToMovie(savedMovie);
    }

    @Override
    public SyncResult syncSingleMovie(Long movieId) {
        MovieSyncContext context = MovieSyncContext.singleMovie(movieId);
        return findSyncStrategy(context).sync(context);
    }

    @Override
    public SyncResult syncAllMovies() {
        MovieSyncContext context = MovieSyncContext.allMovies();
        return findSyncStrategy(context).sync(context);
    }

    private MovieSyncStrategy findSyncStrategy(MovieSyncContext context) {
        return syncStrategies.stream()
                .filter(s -> s.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No strategy found for sync movies context"));
    }
}