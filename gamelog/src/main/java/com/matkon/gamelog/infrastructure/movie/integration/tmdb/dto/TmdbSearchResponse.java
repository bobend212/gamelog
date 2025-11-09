package com.matkon.gamelog.infrastructure.movie.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbSearchResponse {

    private List<TmdbMovieSearchInfoDto> results;
}
