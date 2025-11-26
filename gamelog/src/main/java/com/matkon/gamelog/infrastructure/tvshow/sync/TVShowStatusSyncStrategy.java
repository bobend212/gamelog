package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowStatusSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areStringsDifferent(localTVShow.getStatus(), latestData.getStatus())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("Status")
                    .oldValue(localTVShow.getStatus())
                    .newValue(latestData.getStatus())
                    .build();
            localTVShow.setStatus(latestData.getStatus());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}