package com.matkon.gamelog.api.game.dashboard;

public record GameStats(
        long totalGames,
        long wishlisted,
        long playing,
        long completed,
        long dropped,
        long online
) {
}
