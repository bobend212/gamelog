package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.common.sync.SyncResult;

public interface SyncGamesUseCase {

    SyncResult syncGamesByStatus(GameStatus gameStatus);
}
