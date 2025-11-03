package com.matkon.gamelog.domain.game.sync;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.common.sync.SyncResult;

import java.util.List;

public interface GameSyncStrategy {

    SyncResult sync(List<Game> games);
}

