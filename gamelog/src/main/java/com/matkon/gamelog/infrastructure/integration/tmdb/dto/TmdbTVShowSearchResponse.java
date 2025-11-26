package com.matkon.gamelog.infrastructure.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbTVShowSearchResponse {

    private List<TmdbTVShowSearchInfoDto> results;
}