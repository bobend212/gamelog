package com.matkon.gamelog.domain.tvshow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Season {

    private Long id;
    private String name;
    private int seasonNumber;
    private LocalDate airDate;
    private int episodeCount;
    private int watchedCount;
    private Double rating;
    private TVShow tvShow;
}