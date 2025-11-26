package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowFirstAirDateSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areDatesDifferent(localTVShow.getFirstAirDate(), latestData.getFirstAirDate())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("First Air Date")
                    .oldValue(String.valueOf(localTVShow.getFirstAirDate()))
                    .newValue(String.valueOf(latestData.getFirstAirDate()))
                    .build();
            localTVShow.setFirstAirDate(latestData.getFirstAirDate());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}
