package com.matkon.gamelog.domain.sync.game;

import com.matkon.gamelog.domain.game.model.SyncResult;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;

import java.util.List;

public interface GameSyncStrategy {

    SyncResult sync(List<GameEntity> games);
}

