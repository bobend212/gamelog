package com.matkon.gamelog.domain.game.model.dashboard;

public record GameStatsDto(
        long totalGames,
        long wishlisted,
        long playing,
        long completed,
        long dropped,
        long online,
        double averageRating
) {
}
