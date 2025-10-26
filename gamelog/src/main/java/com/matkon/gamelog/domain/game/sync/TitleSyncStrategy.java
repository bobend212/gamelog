package com.matkon.gamelog.domain.game.sync;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;

import java.util.Optional;

public class TitleSyncStrategy implements FieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(GameEntity localGame, Game latestData) {
        if (SyncUtils.areStringsDifferent(localGame.getTitle(), latestData.getTitle())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("Title")
                    .oldValue(localGame.getTitle())
                    .newValue(latestData.getTitle())
                    .build();
            localGame.setTitle(latestData.getTitle());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}
