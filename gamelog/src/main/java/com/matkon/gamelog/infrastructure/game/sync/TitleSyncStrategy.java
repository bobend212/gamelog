package com.matkon.gamelog.infrastructure.game.sync;

import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.sync.FieldSyncStrategy;

import java.util.Optional;

public class TitleSyncStrategy implements FieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Game localGame, Game latestData) {
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
