package com.matkon.gamelog.domain.sync.game;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;

import java.util.Optional;

public interface FieldSyncStrategy {

    Optional<FieldDifference> syncField(GameEntity localGame, Game latestData);
}

