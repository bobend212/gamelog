package com.matkon.gamelog.api.game.dashboard;

import java.time.LocalDate;

public record RecentGame(
        Long gameId,
        String name,
        LocalDate completedAt
) {
}
