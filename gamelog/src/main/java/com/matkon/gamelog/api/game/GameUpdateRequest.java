package com.matkon.gamelog.api.game;

import com.matkon.gamelog.domain.game.model.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class GameUpdateRequest {
    private String platform;
    private GameStatus status;
    private Double rating;
    private String notes;
    private LocalDate completedAt;
    private boolean favourite;
}
