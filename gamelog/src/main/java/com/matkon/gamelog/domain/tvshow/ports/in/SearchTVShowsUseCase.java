package com.matkon.gamelog.domain.tvshow.ports.in;

import com.matkon.gamelog.domain.tvshow.model.TVShow;

import java.util.List;

public interface SearchTVShowsUseCase {

    List<TVShow> searchTVShows(String query);
}
