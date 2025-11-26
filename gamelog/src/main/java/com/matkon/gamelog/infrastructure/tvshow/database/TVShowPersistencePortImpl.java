package com.matkon.gamelog.infrastructure.tvshow.database;

import com.matkon.gamelog.common.exception.ItemAlreadyExistsException;
import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.tvshow.model.Season;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowInfoPort;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowPersistencePort;
import com.matkon.gamelog.domain.tvshow.sync.TVShowSyncStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class TVShowPersistencePortImpl implements TVShowPersistencePort {

    private final TVShowJpaRepository tvShowJpaRepository;
    private final SeasonJpaRepository seasonJpaRepository;
    private final TVShowInfoPort tvShowInfoPort;
    private final TVShowMapper tvShowMapper;
    private final TVShowSyncStrategy tvShowSyncStrategy;

    @Override
    public Page<TVShow> getAllTVShows(int page, int size, String search, TrackingType trackingType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<TVShowEntity> tvShows = tvShowJpaRepository.findAllBySearchAndTrackingType(search, trackingType, pageable);
        return tvShows.map(tvShowMapper::mapTVShowEntityToTVShow);
    }

    @Override
    public TVShow getSingleTVShow(Long tvShowId) {
        Optional<TVShowEntity> tvShowOpt = tvShowJpaRepository.findById(tvShowId);
        TVShowEntity tvShowEntity = tvShowOpt.orElseThrow(() ->
                new ItemNotFoundException("TV Show with ID '%s' not found in the database".formatted(tvShowId)));

        if (tvShowEntity.getSeasons() != null) {
            tvShowEntity.getSeasons().sort(Comparator.comparing(SeasonEntity::getSeasonNumber));
        }

        return tvShowMapper.mapTVShowEntityToTVShow(tvShowEntity);
    }

    @Override
    @Transactional
    public TVShow saveTVShow(Long tmdbId, TrackingType trackingType) {
        tvShowJpaRepository.findByTmdbId(tmdbId)
                .ifPresent(tvShow -> {
                    throw new ItemAlreadyExistsException(tmdbId);
                });

        TVShow tvShow = Optional.ofNullable(tvShowInfoPort.getSaveTVShowDetails(tmdbId))
                .orElseThrow(() -> new ItemNotFoundException(
                        "TV Show with ID '%s' not found in external API".formatted(tmdbId)));

        tvShow.setVodProviders(tvShowInfoPort.getTVShowVodProviders(tmdbId));
        tvShow.setTrackingType(trackingType);
        tvShow.setUpdatedAt(LocalDateTime.now());

        TVShowEntity tvShowEntity = tvShowMapper.mapTVShowToTVShowEntity(tvShow);

        if (tvShow.getTrackingType() != TrackingType.WISHLIST) {
            for (Season season : tvShow.getSeasons()) {
                if (season.getSeasonNumber() == 0 || season.getEpisodeCount() == 0) {
                    continue; // skip specials or no episodes provided
                }

                SeasonEntity seasonEntity = tvShowMapper.mapSeasonToSeasonEntity(season);
                tvShowEntity.addSeason(seasonEntity);
            }
        }

        TVShowEntity savedTVShow = tvShowJpaRepository.save(tvShowEntity);

        return tvShowMapper.mapTVShowEntityToTVShow(savedTVShow);
    }

    @Override
    public void deleteTVShow(Long tvShowId) {
        if (!tvShowJpaRepository.existsById(tvShowId)) {
            throw new ItemNotFoundException("TV Show with ID '%s' not found in the database".formatted(tvShowId));
        }
        tvShowJpaRepository.deleteById(tvShowId);
    }

    @Override
    @Transactional
    public void updateTrackingType(Long tvShowId, TrackingType trackingType) {
        TVShowEntity existingTVShow = tvShowJpaRepository.findById(tvShowId)
                .orElseThrow(() -> new ItemNotFoundException("TV Show with ID '%s' not found in the database".formatted(tvShowId)));

        existingTVShow.setTrackingType(trackingType);
    }

    @Override
    @Transactional
    public void rateSeason(Long seasonId, Double rating) {
        SeasonEntity existingSeason = seasonJpaRepository.findById(seasonId)
                .orElseThrow(() -> new ItemNotFoundException("Season with ID '%s' not found in the database".formatted(seasonId)));

        existingSeason.setRating(rating);
        existingSeason.getSeries().setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void setWatchedCount(Long seasonId, Integer watchedCount) {
        SeasonEntity existingSeason = seasonJpaRepository.findById(seasonId)
                .orElseThrow(() -> new ItemNotFoundException("Season with ID '%s' not found in the database".formatted(seasonId)));

        int maxEpisodes = existingSeason.getEpisodeCount();
        int newWatchedCount;

        newWatchedCount = Objects.requireNonNullElseGet(watchedCount, () -> existingSeason.getWatchedCount() + 1);

        if (newWatchedCount >= 0 && newWatchedCount <= maxEpisodes) {
            existingSeason.setWatchedCount(newWatchedCount);
            existingSeason.getSeries().setUpdatedAt(LocalDateTime.now());
        }
    }

    @Override
    public SyncResult syncTVShowsByTrackingType(TrackingType trackingType) {
        List<TVShowEntity> tvShows = tvShowJpaRepository.findAll()
                .stream()
                .filter(tvShow -> tvShow.getTrackingType() == trackingType)
                .toList();
        return tvShowSyncStrategy.sync(tvShows.stream().map(tvShowMapper::mapTVShowEntityToTVShow).toList());
    }
}
