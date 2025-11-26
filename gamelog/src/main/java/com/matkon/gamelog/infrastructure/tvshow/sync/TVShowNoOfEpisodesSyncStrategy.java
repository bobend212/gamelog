package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;

public class TVShowNoOfEpisodesSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areIntsDifferent(localTVShow.getNumberOfEpisodes(), latestData.getNumberOfEpisodes())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("No. of episodes")
                    .oldValue(String.valueOf(localTVShow.getNumberOfEpisodes()))
                    .newValue(String.valueOf(latestData.getNumberOfEpisodes()))
                    .build();
            localTVShow.setNumberOfEpisodes(latestData.getNumberOfEpisodes());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}
