package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowNoOfSeasonsSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areIntsDifferent(localTVShow.getNumberOfSeasons(), latestData.getNumberOfSeasons())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("No. of seasons")
                    .oldValue(String.valueOf(localTVShow.getNumberOfSeasons()))
                    .newValue(String.valueOf(latestData.getNumberOfSeasons()))
                    .build();
            localTVShow.setNumberOfSeasons(latestData.getNumberOfSeasons());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}
