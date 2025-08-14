package com.matkon.gamelog.data.tvseries;

public record SeasonProgressDto(
        Long id,
        Long tmdbId,
        int seasonNumber,
        String name,
        int totalEpisodes,
        int watchedCount,
        EpisodeDto lastWatchedEpisode,
        EpisodeDto nextEpisodeToWatch,
        double percentageWatched
) {}

