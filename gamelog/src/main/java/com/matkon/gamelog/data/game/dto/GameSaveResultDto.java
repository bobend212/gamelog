package com.matkon.gamelog.data.game.dto;

import com.matkon.gamelog.data.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameSaveResultDto {
    private Game game;
    private boolean alreadyExists;
    private String message;
}

