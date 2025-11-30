package com.matkon.gamelog.domain.tvshow.service;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import com.matkon.gamelog.domain.tvshow.ports.in.DeleteTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.GetSingleTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.GetTVShowsUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.RateSeasonTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.SaveTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.SearchTVShowsUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.SetWatchCountTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.SyncTVShowsUseCase;
import com.matkon.gamelog.domain.tvshow.ports.in.UpdateTrackingTypeTVShowUseCase;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowInfoPort;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TVShowService implements GetTVShowsUseCase, GetSingleTVShowUseCase,
        SearchTVShowsUseCase, SaveTVShowUseCase, DeleteTVShowUseCase, UpdateTrackingTypeTVShowUseCase,
        RateSeasonTVShowUseCase, SetWatchCountTVShowUseCase, SyncTVShowsUseCase {

    private TVShowPersistencePort tvShowPersistencePort;
    private TVShowInfoPort tvShowInfoPort;

    @Override
    public Page<TVShow> getAllTVShows(int page, int size, String search, TrackingType trackingType) {
        return tvShowPersistencePort.getAllTVShows(page, size, search, trackingType);
    }

    @Override
    @Cacheable(value = "single_tvShow", key = "#tvShowId")
    public TVShow getSingleTVShow(Long tvShowId) {
        return tvShowPersistencePort.getSingleTVShow(tvShowId);
    }

    @Override
    @Cacheable(value = "tv_shows_search_results", key = "#query")
    public List<TVShow> searchTVShows(String query) {
        return tvShowInfoPort.searchTVShows(query);
    }

    @Override
    @Transactional
    @CacheEvict(value = "single_tvShow", allEntries = true)
    public TVShow saveTVShow(Long tmdbId, TrackingType trackingType) {
        return tvShowPersistencePort.saveTVShow(tmdbId, trackingType);
    }

    @Override
    @CacheEvict(value = "single_tvShow", allEntries = true)
    public void deleteTVShow(Long tvShowId) {
        tvShowPersistencePort.deleteTVShow(tvShowId);
    }

    @Override
    @CacheEvict(value = "single_tvShow", allEntries = true)
    public void updateTrackingType(Long tvShowId, TrackingType trackingType) {
        tvShowPersistencePort.updateTrackingType(tvShowId, trackingType);
    }

    @Override
    @CacheEvict(value = "single_tvShow", allEntries = true)
    public void rateSeason(Long seasonId, Double rating) {
        tvShowPersistencePort.rateSeason(seasonId, rating);
    }

    @Override
    @CacheEvict(value = "single_tvShow", allEntries = true)
    public void setWatchedCount(Long seasonId, Integer watchedCount) {
        tvShowPersistencePort.setWatchedCount(seasonId, watchedCount);
    }

    @Override
    public SyncResult syncTVShowsByTrackingType(TrackingType trackingType) {
        return tvShowPersistencePort.syncTVShowsByTrackingType(trackingType);
    }
}