package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;

public interface SaveGameUseCase {

    Game saveGame(Long rawgId, GameStatus gameStatus);
}
