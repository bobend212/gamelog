package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GameUpdate {

    private String platform;
    private GameStatus status;
    private Double rating;
    private String notes;
    private LocalDate completedAt;
    private boolean favourite;
}
