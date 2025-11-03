package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.Optional;

public class MovieReleaseDateSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areDatesDifferent(localMovie.getReleaseDate(), latestData.getReleaseDate())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Release Date")
                    .oldValue(String.valueOf(localMovie.getReleaseDate()))
                    .newValue(String.valueOf(latestData.getReleaseDate()))
                    .build();
            localMovie.setReleaseDate(latestData.getReleaseDate());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}