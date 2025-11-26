package com.matkon.gamelog.domain.tvshow.ports.out;

import com.matkon.gamelog.domain.tvshow.model.TVShow;

import java.util.List;

public interface TVShowInfoPort {

    List<TVShow> searchTVShows(String query);

    TVShow getSaveTVShowDetails(Long tmdbId);

    List<String> getTVShowVodProviders(Long tmdbId);
}
