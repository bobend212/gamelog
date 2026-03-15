package com.matkon.gamelog.api.game.dashboard;


import java.util.List;

public record DashboardResponse(
        GameStats stats,
        List<YearCompletionStat> yearlyCompletions,
        List<RecentGame> recentlyUpdated
) {
}
