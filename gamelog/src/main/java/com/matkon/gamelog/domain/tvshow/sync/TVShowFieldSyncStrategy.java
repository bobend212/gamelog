package com.matkon.gamelog.domain.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.tvshow.model.TVShow;

import java.util.Optional;

public interface TVShowFieldSyncStrategy {

    Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData);
}
