package com.matkon.gamelog.domain.tvshow.ports.out;

import com.matkon.gamelog.domain.tvshow.model.TVShow;

import java.util.List;
import java.util.Set;

public interface TVShowInfoPort {

    List<TVShow> searchTVShows(String query);

    TVShow getSaveTVShowDetails(Long tmdbId);

    Set<String> getTVShowVodProviders(Long tmdbId);
}
