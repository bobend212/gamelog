package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.ports.out.MovieInfoPort;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;
import com.matkon.gamelog.domain.movie.sync.MovieSyncContext;
import com.matkon.gamelog.domain.movie.sync.MovieSyncStrategy;
import com.matkon.gamelog.infrastructure.movie.database.MovieJpaRepository;
import com.matkon.gamelog.infrastructure.movie.database.MovieMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllMoviesSyncStrategy implements MovieSyncStrategy {

    private final MovieInfoPort movieInfoPort;
    private final MovieJpaRepository movieJpaRepository;
    private final List<MovieFieldSyncStrategy> movieFieldSyncStrategies;
    private final MovieMapper movieMapper;

    public AllMoviesSyncStrategy(MovieInfoPort movieInfoPort, MovieJpaRepository movieJpaRepository, MovieMapper movieMapper) {
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
        return syncAllMovies();
    }

    @Override
    public boolean supports(MovieSyncContext context) {
        return context.type() == MovieSyncContext.SyncType.ALL_MOVIES;
    }

    private SyncResult syncAllMovies() {
        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        List<Movie> movies = movieJpaRepository.findAll()
                .stream()
                .map(movieMapper::mapMovieEntityToMovie)
                .toList();

        for (Movie localMovie : movies) {
            Movie latestMovieData = movieInfoPort.getSaveMovieDetails(localMovie.getTmdbId());
            latestMovieData.setVodProviders(movieInfoPort.getVodProviders(localMovie.getTmdbId()));

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
        }

        return new SyncResult(movies.size(), updatedCount, changes);
    }
}