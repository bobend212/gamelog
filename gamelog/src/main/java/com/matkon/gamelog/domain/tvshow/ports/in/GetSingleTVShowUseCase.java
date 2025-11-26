package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.tvshow.model.TVShow;

public interface GetSingleTVShowUseCase {

    TVShow getSingleTVShow(Long tvShowId);
}
