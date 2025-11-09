package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.Optional;

public class MovieTitleSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areStringsDifferent(localMovie.getTitle(), latestData.getTitle())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Title")
                    .oldValue(localMovie.getTitle())
                    .newValue(latestData.getTitle())
                    .build();
            localMovie.setTitle(latestData.getTitle());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}