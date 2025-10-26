package com.matkon.gamelog.domain.game.sync;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;

import java.util.Optional;

public class ReleaseDateSyncStrategy implements FieldSyncStrategy {
    @Override
    public Optional<FieldDifference> syncField(GameEntity localGame, Game latestData) {
        if (SyncUtils.areDatesDifferent(localGame.getReleaseDate(), latestData.getReleaseDate())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("ReleaseDate")
                    .oldValue(String.valueOf(localGame.getReleaseDate()))
                    .newValue(String.valueOf(latestData.getReleaseDate()))
                    .build();
            localGame.setReleaseDate(latestData.getReleaseDate());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}
