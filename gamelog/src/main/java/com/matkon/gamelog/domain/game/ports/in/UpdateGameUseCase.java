package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameUpdate;

public interface UpdateGameUseCase {

    Game updateGame(Long id, GameUpdate updateRequest);
}
