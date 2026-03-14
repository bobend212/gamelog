package com.matkon.gamelog.domain.game.model.dashboard;

import java.time.LocalDate;

public record RecentGameDto(
        Long gameId,
        String name,
        LocalDate completedAt
) {
}
