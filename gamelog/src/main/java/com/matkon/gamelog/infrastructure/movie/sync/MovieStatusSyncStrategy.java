package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.Optional;

public class MovieStatusSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areStringsDifferent(localMovie.getStatus(), latestData.getStatus())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Status")
                    .oldValue(localMovie.getStatus())
                    .newValue(latestData.getStatus())
                    .build();
            localMovie.setStatus(latestData.getStatus());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}