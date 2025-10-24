package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.SyncResult;

public interface SyncGamesUseCase {

    SyncResult syncGamesByStatus(GameStatus gameStatus);
}
