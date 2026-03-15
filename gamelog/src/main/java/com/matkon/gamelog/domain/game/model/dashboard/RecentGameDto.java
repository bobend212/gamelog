package com.matkon.gamelog.domain.game.model.dashboard;

import com.matkon.gamelog.domain.game.model.GameStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecentGameDto(
        Long gameId,
        String title,
        LocalDateTime updatedAt,
        GameStatus status,
        LocalDate releaseDate,
        String imageUrl
) {
}
