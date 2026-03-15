package com.matkon.gamelog.domain.game.model.dashboard;


import java.util.List;

public record DashboardDto(
        GameStatsDto stats,
        List<YearCompletionStatDto> yearlyCompletions,
        List<RecentGameDto> recentlyUpdated
) {
}
