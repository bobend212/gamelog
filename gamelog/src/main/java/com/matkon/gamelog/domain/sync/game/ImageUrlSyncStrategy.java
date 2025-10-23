package com.matkon.gamelog.domain.sync.game;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.sync.SyncUtils;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;

import java.util.Optional;

public class ImageUrlSyncStrategy implements FieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(GameEntity localGame, Game latestData) {
        if (SyncUtils.areStringsDifferent(localGame.getImageUrl(), latestData.getImageUrl())) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("ImageUrl")
                    .oldValue(localGame.getImageUrl())
                    .newValue(latestData.getImageUrl())
                    .build();
            localGame.setImageUrl(latestData.getImageUrl());
            return Optional.of(diff);
        }
        return Optional.empty();
    }
}