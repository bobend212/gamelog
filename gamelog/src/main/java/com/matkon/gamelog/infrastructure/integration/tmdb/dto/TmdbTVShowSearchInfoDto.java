package com.matkon.gamelog.infrastructure.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbTVShowSearchInfoDto {

    private Long id;
    private String name;
    private String poster_path;
    private String first_air_date;
}