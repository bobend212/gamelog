package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;

public interface SaveTVShowUseCase {

    TVShow saveTVShow(Long tmdbId, TrackingType trackingType);
}
