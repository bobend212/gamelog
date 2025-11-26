package com.matkon.gamelog.domain.tvshow.ports.out;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import org.springframework.data.domain.Page;

public interface TVShowPersistencePort {

    Page<TVShow> getAllTVShows(int page, int size, String search, TrackingType trackingType);

    TVShow getSingleTVShow(Long tvShowId);

    TVShow saveTVShow(Long tmdbId, TrackingType trackingType);

    void deleteTVShow(Long tvShowId);

    void updateTrackingType(Long tvShowId, TrackingType trackingType);

    void rateSeason(Long seasonId, Double rating);

    void setWatchedCount(Long seasonId, Integer watchedCount);

    SyncResult syncTVShowsByTrackingType(TrackingType trackingType);
}
