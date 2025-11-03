package com.matkon.gamelog.api.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private LocalDate releaseDatePL;
    private int runtime;
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;
    private LocalDateTime createdAt;
}