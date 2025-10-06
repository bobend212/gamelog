package com.matkon.gamelog.data.tvshow.dto;

import com.matkon.gamelog.data.tvshow.TVShow;
import com.matkon.gamelog.data.tvshow.TrackingType;
import com.matkon.gamelog.data.tvshow.season.Season;
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
public class TVShowListDto {
    private Long id;
    private Long tmdbId;
    private String name;
    private LocalDate first_air_date;
    private int number_of_episodes;
    private int number_of_seasons;
    private String poster_path;
    private LocalDate last_air_date;
    private String status;
    private TrackingType trackingType;
    private int totalWatchedEpisodes;
    private int percentageProgress;
    private Double ratingOverall;
    private LocalDateTime updatedAt;
    private String nextEpisode;
    private List<String> vodProviders = new ArrayList<>();

    public static TVShowListDto fromEntity(TVShow tvShow) {
        TVShowListDto dto = new TVShowListDto();
        dto.id = tvShow.getId();
        dto.tmdbId = tvShow.getTmdbId();
        dto.name = tvShow.getName();
        dto.first_air_date = tvShow.getFirst_air_date();
        dto.number_of_episodes = tvShow.getNumber_of_episodes();
        dto.number_of_seasons = tvShow.getNumber_of_seasons();
        dto.poster_path = tvShow.getPoster_path();
        dto.last_air_date = tvShow.getLast_air_date();
        dto.status = tvShow.getStatus();
        dto.trackingType = tvShow.getTrackingType();
        dto.updatedAt = tvShow.getUpdatedAt();

        int totalWatched = 0;
        if (tvShow.getSeasons() != null) {
            for (Season season : tvShow.getSeasons()) {
                totalWatched += season.getWatchedCount();
            }
        }
        dto.totalWatchedEpisodes = totalWatched;

        if (dto.number_of_episodes > 0) {
            dto.percentageProgress = (int) Math.round((totalWatched * 100.0) / dto.number_of_episodes);
        } else {
            dto.percentageProgress = 0;
        }

        if (tvShow.getSeasons() != null && !tvShow.getSeasons().isEmpty()) {
            double avgRating = tvShow.getSeasons().stream()
                    .filter(s -> s.getRating() != null)
                    .mapToDouble(Season::getRating)
                    .average()
                    .orElse(0.0);
            double roundedAvg = Math.round(avgRating * 10) / 10.0;  // rounds to 1 decimal place
            dto.setRatingOverall(roundedAvg);
        } else {
            dto.setRatingOverall(null);
        }

        String nextEp = null;
        if (tvShow.getSeasons() != null) {
            for (Season season : tvShow.getSeasons()) {
                int watched = season.getWatchedCount();
                int totalEps = season.getEpisode_count();
                if (watched < totalEps) {
                    nextEp = String.format("S%02d:E%02d", season.getSeason_number(), watched + 1);
                    break;
                }
            }
        }
        dto.setNextEpisode(nextEp);

        dto.setVodProviders(tvShow.getVodProviders());

        return dto;
    }
}