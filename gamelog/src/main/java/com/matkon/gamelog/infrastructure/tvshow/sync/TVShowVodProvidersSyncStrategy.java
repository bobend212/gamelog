package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TVShowVodProvidersSyncStrategy implements TVShowFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(TVShow localTVShow, TVShow latestData) {
        Set<String> oldProviders = localTVShow.getVodProviders().stream()
                .map(this::extractProviderName)
                .collect(Collectors.toSet());

        Set<String> newProviders = latestData.getVodProviders().stream()
                .map(this::extractProviderName)
                .collect(Collectors.toSet());

        if (!oldProviders.equals(newProviders)) {
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

    private String extractProviderName(String provider) {
        String[] parts = provider.split(";", 2);
        return parts.length > 1 ? parts[1] : parts[0];
    }
}