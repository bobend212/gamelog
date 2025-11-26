package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.tvshow.model.TrackingType;

public interface UpdateTrackingTypeTVShowUseCase {

    void updateTrackingType(Long tvShowId, TrackingType trackingType);
}
