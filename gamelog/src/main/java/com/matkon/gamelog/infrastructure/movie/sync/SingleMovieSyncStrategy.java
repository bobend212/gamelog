package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.ports.out.MovieInfoPort;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;
import com.matkon.gamelog.domain.movie.sync.MovieSyncContext;
import com.matkon.gamelog.domain.movie.sync.MovieSyncStrategy;
import com.matkon.gamelog.infrastructure.movie.database.MovieEntity;
import com.matkon.gamelog.infrastructure.movie.database.MovieJpaRepository;
import com.matkon.gamelog.infrastructure.movie.database.MovieMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SingleMovieSyncStrategy implements MovieSyncStrategy {

    private final MovieInfoPort movieInfoPort;
    private final MovieJpaRepository movieJpaRepository;
    private final List<MovieFieldSyncStrategy> movieFieldSyncStrategies;
    private final MovieMapper movieMapper;

    public SingleMovieSyncStrategy(MovieInfoPort movieInfoPort, MovieJpaRepository movieJpaRepository, MovieMapper movieMapper) {
        this.movieInfoPort = movieInfoPort;
        this.movieJpaRepository = movieJpaRepository;
        this.movieMapper = movieMapper;
        this.movieFieldSyncStrategies = List.of(
                new MovieTitleSyncStrategy(),
                new MovieOriginalTitleSyncStrategy(),
                new MovieReleaseDateSyncStrategy(),
                new MovieStatusSyncStrategy(),
                new MoviePosterSyncStrategy(),
                new MovieVodProvidersSyncStrategy()
        );
    }

    @Override
    public SyncResult sync(MovieSyncContext context) {
        if (!supports(context)) {
            throw new UnsupportedOperationException("Context not supported");
        }
        return syncSingleMovie(context.movieId());
    }

    @Override
    public boolean supports(MovieSyncContext context) {
        return context.type() == MovieSyncContext.SyncType.SINGLE_MOVIE;
    }

    private SyncResult syncSingleMovie(Long movieId) {
        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        Optional<MovieEntity> movieOpt = movieJpaRepository.findById(movieId);
        MovieEntity movieEntity = movieOpt.orElseThrow(() ->
                new ItemNotFoundException("Movie with ID '%s' not found in the database".formatted(movieId)));

        Movie localMovie = movieMapper.mapMovieEntityToMovie(movieEntity);
        Movie latestMovieData = movieInfoPort.getSaveMovieDetails(localMovie.getTmdbId());
        latestMovieData.setVodProviders(movieInfoPort.getMovieVodProviders(localMovie.getTmdbId()));

        boolean changed = false;
        for (MovieFieldSyncStrategy fieldSyncStrategy : movieFieldSyncStrategies) {
            boolean thisChanged = fieldSyncStrategy.syncField(localMovie, latestMovieData)
                    .map(changes::add)
                    .orElse(false);
            changed = changed || thisChanged;
        }

        if (changed) {
            movieJpaRepository.save(movieMapper.mapMovieToMovieEntity(localMovie));
            updatedCount++;
        }


        return new SyncResult(1, updatedCount, changes);
    }
}

