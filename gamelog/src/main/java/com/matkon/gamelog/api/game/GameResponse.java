package com.matkon.gamelog.api.game;

import com.matkon.gamelog.domain.game.model.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
class GameResponse {
    private Long id;
    private GameStatus status;
    private Double rating;
    private String notes;
    private String platform;
    private boolean favourite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate completedAt;
    private Long rawgId;
    private String title;
    private LocalDate releaseDate;
    private String imageUrl;
}
