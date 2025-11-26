package com.matkon.gamelog.api.tvshow;

import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TVShowListResponse {

    private Long id;
    private Long tmdbId;
    private String name;
    private LocalDate firstAirDate;
    private int numberOfEpisodes;
    private int numberOfSeasons;
    private String posterPath;
    private LocalDate lastAirDate;
    private String status;
    private TrackingType trackingType;
    private int totalWatchedEpisodes;
    private int percentageProgress;
    private Double ratingOverall;
    private LocalDateTime updatedAt;
    private String nextEpisode;
    private List<String> vodProviders = new ArrayList<>();
}