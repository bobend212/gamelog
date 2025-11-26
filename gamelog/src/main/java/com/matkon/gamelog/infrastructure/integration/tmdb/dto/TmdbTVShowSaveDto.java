package com.matkon.gamelog.infrastructure.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TmdbTVShowSaveDto {

    private Long id;
    private String name;
    private String poster_path;
    private List<TmdbSeasonSaveDto> seasons;
    private String status;
    private LocalDate first_air_date;
    private int number_of_episodes;
    private int number_of_seasons;
    private LocalDate last_air_date;
}
