package com.matkon.gamelog.infrastructure.game.sync;

import com.matkon.gamelog.domain.common.sync.SyncUtils;
import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.sync.FieldSyncStrategy;

import java.util.Optional;

public class ImageUrlSyncStrategy implements FieldSyncStrategy {

    @Override
    public Optional<FieldDifference> syncField(Game localGame, Game latestData) {
        String localUrl = localGame.getImageUrl();
        String latestUrl = latestData.getImageUrl();
        if (SyncUtils.areStringsDifferent(localUrl, latestUrl)) {
            FieldDifference diff = FieldDifference.builder()
                    .title(latestData.getTitle())
                    .fieldName("ImageUrl")
                    .oldValue(extractFileName(localUrl))
                    .newValue(extractFileName(latestUrl))
                    .build();
            localGame.setImageUrl(latestUrl);
            return Optional.of(diff);
        }
        return Optional.empty();
    }

    private String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        int lastSlash = url.lastIndexOf('/');
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }
}