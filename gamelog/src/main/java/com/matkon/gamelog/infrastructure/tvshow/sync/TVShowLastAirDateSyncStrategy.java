package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowLastAirDateSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areDatesDifferent(localTVShow.getLastAirDate(), latestData.getLastAirDate())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("Last Air Date")
                    .oldValue(String.valueOf(localTVShow.getLastAirDate()))
                    .newValue(String.valueOf(latestData.getLastAirDate()))
                    .build();
            localTVShow.setLastAirDate(latestData.getLastAirDate());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}
