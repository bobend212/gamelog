package com.matkon.gamelog.infrastructure.tvshow.sync;

import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.tvshow.model.Season;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowInfoPort;
import com.matkon.gamelog.domain.tvshow.sync.TVShowFieldSyncStrategy;
import com.matkon.gamelog.domain.tvshow.sync.TVShowSyncStrategy;
import com.matkon.gamelog.infrastructure.tvshow.database.SeasonEntity;
import com.matkon.gamelog.infrastructure.tvshow.database.TVShowEntity;
import com.matkon.gamelog.infrastructure.tvshow.database.TVShowJpaRepository;
import com.matkon.gamelog.infrastructure.tvshow.database.TVShowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultTVShowSyncStrategy implements TVShowSyncStrategy {

    private final TVShowInfoPort tvShowInfoPort;
    private final TVShowJpaRepository tvShowJpaRepository;
    private final List<TVShowFieldSyncStrategy> fieldSyncStrategies;
    private final TVShowMapper tvShowMapper;

    public DefaultTVShowSyncStrategy(TVShowInfoPort tvShowInfoPort, TVShowJpaRepository tvShowJpaRepository, TVShowMapper tvShowMapper) {
        this.tvShowInfoPort = tvShowInfoPort;
        this.tvShowJpaRepository = tvShowJpaRepository;
        this.tvShowMapper = tvShowMapper;
        this.fieldSyncStrategies = List.of(
                new TVShowNameSyncStrategy(),
                new TVShowFirstAirDateSyncStrategy(),
                new TVShowNoOfEpisodesSyncStrategy(),
                new TVShowNoOfSeasonsSyncStrategy(),
                new TVShowPosterPathSyncStrategy(),
                new TVShowLastAirDateSyncStrategy(),
                new TVShowStatusSyncStrategy(),
                new TVShowVodProvidersSyncStrategy()
        );
    }

    @Override
    public SyncResult sync(List<TVShow> tvShows) {
        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        for (TVShow localTVShow : tvShows) {
            TVShow latestData = tvShowInfoPort.getSaveTVShowDetails(localTVShow.getTmdbId());
            if (latestData == null) continue;

            latestData.setVodProviders(tvShowInfoPort.getTVShowVodProviders(localTVShow.getTmdbId()));

            boolean changed = false;

            for (TVShowFieldSyncStrategy fieldSyncStrategy : fieldSyncStrategies) {
                boolean thisChanged = fieldSyncStrategy.syncField(localTVShow, latestData)
                        .map(changes::add)
                        .orElse(false);
                changed = changed || thisChanged;
            }

            TVShowEntity tvShowEntity = tvShowJpaRepository.findByTmdbId(localTVShow.getTmdbId())
                    .orElseThrow(() -> new ItemNotFoundException("TV Show not found in DB"));

            if (changed) {
                tvShowMapper.updateEntityFromDomain(localTVShow, tvShowEntity);
            }

            if (localTVShow.getTrackingType() != TrackingType.WISHLIST) {
                if (syncSeasons(tvShowEntity, latestData.getSeasons(), changes)) {
                    changed = true;
                }
            }

            if (changed) {
                tvShowEntity.setVodProviders(localTVShow.getVodProviders());
                tvShowJpaRepository.save(tvShowEntity);
                updatedCount++;
            }
        }

        return new SyncResult(tvShows.size(), updatedCount, changes);
    }

    /**
     * Merges the API season data into the existing Entity list.
     * Returns true if any change occurred.
     */
    private boolean syncSeasons(TVShowEntity tvShowEntity, Set<Season> apiSeasons, List<FieldDifference> changes) {
        boolean anyChange = false;

        Map<Integer, SeasonEntity> existingSeasonsMap = tvShowEntity.getSeasons().stream()
                .collect(Collectors.toMap(SeasonEntity::getSeasonNumber, Function.identity()));

        for (Season apiSeason : apiSeasons) {
            if (apiSeason.getSeasonNumber() == 0 || apiSeason.getEpisodeCount() == 0) continue;

            SeasonEntity existingSeason = existingSeasonsMap.get(apiSeason.getSeasonNumber());

            if (existingSeason != null) {
                // --- UPDATE EXISTING SEASON ---
                if (updateSeasonFields(existingSeason, apiSeason, changes)) {
                    anyChange = true;
                }
            } else {
                // --- ADD NEW SEASON ---
                SeasonEntity newSeasonEntity = tvShowMapper.mapSeasonToSeasonEntity(apiSeason);
                tvShowEntity.addSeason(newSeasonEntity);

                changes.add(FieldDifference.builder()
                        .title(tvShowEntity.getName() + " - New Season")
                        .fieldName("Season " + apiSeason.getSeasonNumber())
                        .oldValue("")
                        .newValue(String.valueOf(apiSeason.getSeasonNumber()))
                        .build());
                anyChange = true;
            }
        }

        return anyChange;
    }

    private boolean updateSeasonFields(SeasonEntity target, Season source, List<FieldDifference> changes) {
        boolean changed = false;

        if (SyncUtils.areStringsDifferent(target.getName(), source.getName())) {
            changes.add(FieldDifference.builder()
                    .title(target.getTvShow().getName() + " - Season: " + target.getSeasonNumber())
                    .fieldName("Name")
                    .oldValue(target.getName())
                    .newValue(source.getName())
                    .build());
            target.setName(source.getName());
            changed = true;
        }
        if (SyncUtils.areDatesDifferent(target.getAirDate(), source.getAirDate())) {
            changes.add(FieldDifference.builder()
                    .title(target.getTvShow().getName() + " - Season: " + target.getSeasonNumber())
                    .fieldName("Air Date")
                    .oldValue(String.valueOf(target.getAirDate()))
                    .newValue(String.valueOf(source.getAirDate()))
                    .build());
            target.setAirDate(source.getAirDate());
            changed = true;
        }
        if (SyncUtils.areIntsDifferent(target.getEpisodeCount(), source.getEpisodeCount())) {
            changes.add(FieldDifference.builder()
                    .title(target.getTvShow().getName() + " - Season: " + target.getSeasonNumber())
                    .fieldName("No. of episodes")
                    .oldValue(String.valueOf(target.getEpisodeCount()))
                    .newValue(String.valueOf(source.getEpisodeCount()))
                    .build());
            target.setEpisodeCount(source.getEpisodeCount());
            changed = true;
        }
        return changed;
    }
}