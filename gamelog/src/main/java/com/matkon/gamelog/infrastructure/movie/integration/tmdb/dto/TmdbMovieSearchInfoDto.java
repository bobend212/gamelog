package com.matkon.gamelog.infrastructure.movie.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbMovieSearchInfoDto {

    private Long id;
    private String poster_path;
    private String title;
    private String release_date;
}
