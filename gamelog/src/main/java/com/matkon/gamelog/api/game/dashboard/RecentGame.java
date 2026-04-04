package com.matkon.gamelog.api.game.dashboard;

import com.matkon.gamelog.domain.game.model.GameStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record RecentGame(
        Long id,
        String title,
        OffsetDateTime updatedAt,
        GameStatus status,
        LocalDate releaseDate,
        String imageUrl
) {
}
