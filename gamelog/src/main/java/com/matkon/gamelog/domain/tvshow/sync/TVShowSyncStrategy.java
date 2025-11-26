package com.matkon.gamelog.domain.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.tvshow.model.TVShow;

import java.util.List;

public interface TVShowSyncStrategy {

    SyncResult sync(List<TVShow> tvShows);
}
