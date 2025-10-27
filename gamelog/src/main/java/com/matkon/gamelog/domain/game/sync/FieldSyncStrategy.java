package com.matkon.gamelog.domain.game.sync;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;

import java.util.Optional;

public interface FieldSyncStrategy {

    Optional<FieldDifference> syncField(Game localGame, Game latestData);
}

