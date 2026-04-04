package com.matkon.gamelog.domain.game.model.dashboard;

import com.matkon.gamelog.domain.game.model.GameStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record RecentGameDto(
        Long id,
        String title,
        OffsetDateTime updatedAt,
        GameStatus status,
        LocalDate releaseDate,
        String imageUrl
) {
}
