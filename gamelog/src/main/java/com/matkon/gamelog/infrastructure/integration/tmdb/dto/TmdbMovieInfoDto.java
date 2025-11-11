package com.matkon.gamelog.infrastructure.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbMovieInfoDto {

    private String overview;
    private int runtime;
}
