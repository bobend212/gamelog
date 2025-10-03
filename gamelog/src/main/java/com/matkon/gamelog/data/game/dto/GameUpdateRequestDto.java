package com.matkon.gamelog.data.game.dto;

import com.matkon.gamelog.data.game.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameUpdateRequestDto {
    private String platform;
    private GameStatus status;
    private Double rating;
    private String notes;
    private LocalDate completedAt;
    private boolean favourite;
}
