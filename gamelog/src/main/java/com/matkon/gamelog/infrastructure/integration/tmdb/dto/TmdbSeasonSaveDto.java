package com.matkon.gamelog.infrastructure.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TmdbSeasonSaveDto {

    private String name;
    private int season_number;
    private LocalDate air_date;
    private int episode_count;
}
