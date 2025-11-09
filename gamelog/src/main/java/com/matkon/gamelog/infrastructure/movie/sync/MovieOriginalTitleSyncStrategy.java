package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.Optional;

public class MovieOriginalTitleSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areStringsDifferent(localMovie.getOriginalTitle(), latestData.getOriginalTitle())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Original Title")
                    .oldValue(localMovie.getOriginalTitle())
                    .newValue(latestData.getOriginalTitle())
                    .build();
            localMovie.setOriginalTitle(latestData.getOriginalTitle());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}