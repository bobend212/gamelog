package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowNameSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areStringsDifferent(localTVShow.getName(), latestData.getName())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("Name")
                    .oldValue(localTVShow.getName())
                    .newValue(latestData.getName())
                    .build();
            localTVShow.setName(latestData.getName());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}
