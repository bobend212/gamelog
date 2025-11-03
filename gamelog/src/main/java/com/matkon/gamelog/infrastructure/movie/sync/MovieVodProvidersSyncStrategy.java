package com.matkon.gamelog.infrastructure.movie.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.sync.MovieFieldSyncStrategy;

import java.util.List;
import java.util.Optional;

public class MovieVodProvidersSyncStrategy implements MovieFieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Movie localMovie, Movie latestData) {
        if (SyncUtils.areStringListsDifferent(localMovie.getVodProviders(), latestData.getVodProviders())) {
            List<String> oldProviders = localMovie.getVodProviders().stream()
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
                    .title(latestData.getTitle())
                    .fieldName("VOD Providers")
                    .oldValue(String.valueOf(oldProviders))
                    .newValue(String.valueOf(newProviders))
                    .build();
            localMovie.setVodProviders(latestData.getVodProviders());
            return Optional.of(diff);
        }

        return Optional.empty();
    }
}