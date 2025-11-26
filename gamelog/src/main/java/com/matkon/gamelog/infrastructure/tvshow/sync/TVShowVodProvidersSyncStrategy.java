package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.List;
import java.util.Optional;

public class TVShowVodProvidersSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        if (SyncUtils.areStringListsDifferent(localTVShow.getVodProviders(), latestData.getVodProviders())) {
            List<String> oldProviders = localTVShow.getVodProviders().stream()
                    .map(s -> {
                        String[] parts = s.split(";", 2);
                        return parts.length > 1 ? parts[1] : parts[0];
                    }).toList();

            List<String> newProviders = latestData.getVodProviders().stream()
                    .map(s -> {
                        String[] parts = s.split(";", 2);
                        return parts.length > 1 ? parts[1] : parts[0];
                    }).toList();

            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getName())
                    .fieldName("VOD Providers")
                    .oldValue(String.valueOf(oldProviders))
                    .newValue(String.valueOf(newProviders))
                    .build();
            localTVShow.setVodProviders(latestData.getVodProviders());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}