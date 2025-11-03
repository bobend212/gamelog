package com.matkon.gamelog.domain.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.movie.model.Movie;

import java.util.Optional;

public interface MovieFieldSyncStrategy {

    Optional<FieldDifference> syncField(Movie localMovie, Movie latestData);
}
