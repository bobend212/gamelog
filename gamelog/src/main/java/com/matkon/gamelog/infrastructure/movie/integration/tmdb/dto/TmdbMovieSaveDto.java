package com.matkon.gamelog.infrastructure.movie.integration.tmdb.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TmdbMovieSaveDto {

    private Long id;
    private String title;
    private String original_title;
    private String poster_path;
    private String status;
    private LocalDate release_date;
    private List<TmdbMovieSaveGenreDto> genres;
}
