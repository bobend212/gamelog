package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;

public interface SyncTVShowsUseCase {

    SyncResult syncTVShowsByTrackingType(TrackingType trackingType);
}
