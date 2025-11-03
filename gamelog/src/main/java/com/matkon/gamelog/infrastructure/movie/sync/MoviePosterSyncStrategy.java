package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.Optional;

public class MoviePosterSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areStringsDifferent(localMovie.getPoster(), latestData.getPoster())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Poster")
                    .oldValue(localMovie.getPoster())
                    .newValue(latestData.getPoster())
                    .build();
            localMovie.setPoster(latestData.getPoster());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}