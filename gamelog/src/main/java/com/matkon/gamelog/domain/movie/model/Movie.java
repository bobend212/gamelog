package com.matkon.gamelog.domain.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private LocalDate releaseDate;
    private LocalDate releaseDatePL;
    private String status;
    private String poster;
    private Set<String> genres;
    private Set<String> vodProviders;
    private LocalDateTime createdAt;
    private String overview;
    private int runtime;
}
