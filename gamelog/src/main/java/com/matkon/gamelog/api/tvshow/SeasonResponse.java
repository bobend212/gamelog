package com.matkon.gamelog.api.tvshow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeasonResponse {

    private Long id;
    private String name;
    private int seasonNumber;
    private LocalDate airDate;
    private int episodeCount;
    private int watchedCount;
    private Double rating;
}