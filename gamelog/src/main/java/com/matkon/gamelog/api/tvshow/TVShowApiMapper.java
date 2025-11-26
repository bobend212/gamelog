package com.matkon.gamelog.api.tvshow;

import com.matkon.gamelog.domain.tvshow.model.Season;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TVShowApiMapper {

    @Mapping(target = "totalWatchedEpisodes", expression = "java(getTotalWatched(tvShow))")
    @Mapping(target = "ratingOverall", expression = "java(getRatingOverall(tvShow))")
    @Mapping(target = "nextEpisode", expression = "java(getNextEpisode(tvShow))")
    TVShowListResponse mapTVShowToTVShowListResponse(TVShow tvShow);

    @Mapping(target = "totalWatchedEpisodes", expression = "java(getTotalWatched(tvShow))")
    @Mapping(target = "ratingOverall", expression = "java(getRatingOverall(tvShow))")
    @Mapping(target = "nextEpisode", expression = "java(getNextEpisode(tvShow))")
    TVShowResponse mapTVShowToTVShowResponse(TVShow tvShow);

    TVShowSearchResponse mapTVShowToTVShowSearchResponse(TVShow tvShow);

    default int getTotalWatched(TVShow tvShow) {
        int totalWatched = 0;
        if (tvShow.getSeasons() != null) {
            for (Season season : tvShow.getSeasons()) {
                totalWatched += season.getWatchedCount();
            }
        }
        return totalWatched;
    }

    default int getPercentageProgress(int numberOfEpisodes, int totalWatchedEpisodes) {
        int percentageProgress = 0;
        if (numberOfEpisodes > 0) {
            percentageProgress = (int) Math.round((totalWatchedEpisodes * 100.0) / numberOfEpisodes);
        }
        return percentageProgress;
    }

    @AfterMapping
    default void calculatePercentageProgress(@MappingTarget TVShowResponse dto) {
        int calculatedProgress = getPercentageProgress(dto.getNumberOfEpisodes(), dto.getTotalWatchedEpisodes());
        dto.setPercentageProgress(calculatedProgress);
    }

    @AfterMapping
    default void calculatePercentageProgressList(@MappingTarget TVShowListResponse dto) {
        int calculatedProgress = getPercentageProgress(dto.getNumberOfEpisodes(), dto.getTotalWatchedEpisodes());
        dto.setPercentageProgress(calculatedProgress);
    }

    default Double getRatingOverall(TVShow tvShow) {
        if (tvShow.getSeasons() != null && !tvShow.getSeasons().isEmpty()) {
            double avgRating = tvShow.getSeasons().stream()
                    .filter(s -> s.getRating() != null)
                    .mapToDouble(Season::getRating)
                    .average()
                    .orElse(0.0);
            return Math.round(avgRating * 10) / 10.0;
        } else {
            return null;
        }
    }

    default String getNextEpisode(TVShow tvShow) {
        String nextEp = null;
        if (tvShow.getSeasons() != null) {
            for (Season season : tvShow.getSeasons()) {
                int watched = season.getWatchedCount();
                int totalEps = season.getEpisodeCount();
                if (watched < totalEps) {
                    nextEp = String.format("S%02d:E%02d", season.getSeasonNumber(), watched + 1);
                    break;
                }
            }
        }
        return nextEp;
    }
}