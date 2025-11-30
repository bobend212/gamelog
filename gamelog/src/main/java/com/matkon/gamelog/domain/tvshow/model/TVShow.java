package com.matkon.gamelog.domain.tvshow.model;

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
public class TVShow {

    private Long id;
    private Long tmdbId;
    private String name;
    private LocalDate firstAirDate;
    private int numberOfEpisodes;
    private int numberOfSeasons;
    private String posterPath;
    private LocalDate lastAirDate;
    private String status;
    private LocalDateTime updatedAt;
    private TrackingType trackingType;
    private Set<Season> seasons;
    private Set<String> vodProviders;
}