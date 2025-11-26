package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import org.springframework.data.domain.Page;

public interface GetTVShowsUseCase {

    Page<TVShow> getAllTVShows(int page, int size, String search, TrackingType trackingType);
}
