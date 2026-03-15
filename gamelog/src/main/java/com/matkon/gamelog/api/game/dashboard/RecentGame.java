package com.matkon.gamelog.api.game.dashboard;

import com.matkon.gamelog.domain.game.model.GameStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecentGame(
        Long gameId,
        String title,
        LocalDateTime updatedAt,
        GameStatus status,
        LocalDate releaseDate,
        String imageUrl
) {
}
