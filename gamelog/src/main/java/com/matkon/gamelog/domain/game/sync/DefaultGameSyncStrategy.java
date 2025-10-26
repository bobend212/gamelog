package com.matkon.gamelog.domain.game.sync;

import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.SyncResult;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;
import com.matkon.gamelog.infrastructure.game.database.GameJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultGameSyncStrategy implements GameSyncStrategy {

    private final GameInfoPort gameInfoPort;
    private final GameJpaRepository gameJpaRepository;
    private final List<FieldSyncStrategy> fieldSyncStrategies;

    public DefaultGameSyncStrategy(GameInfoPort gameInfoPort, GameJpaRepository repository) {
        this.gameInfoPort = gameInfoPort;
        this.gameJpaRepository = repository;
        this.fieldSyncStrategies = List.of(
                new ReleaseDateSyncStrategy(),
                new TitleSyncStrategy(),
                new ImageUrlSyncStrategy()
        );
    }

    @Override
    public SyncResult sync(List<GameEntity> gameEntities) {
        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        for (GameEntity localGame : gameEntities) {
            Game latestData = gameInfoPort.getGameDetails(localGame.getRawgId());
            if (latestData == null) continue;

            boolean changed = false;
            for (FieldSyncStrategy fieldSyncStrategy : fieldSyncStrategies) {
                boolean thisChanged = fieldSyncStrategy.syncField(localGame, latestData)
                        .map(changes::add)
                        .orElse(false);
                changed = changed || thisChanged;
            }

            if (changed) {
                gameJpaRepository.save(localGame);
                updatedCount++;
            }
        }

        return new SyncResult(gameEntities.size(), updatedCount, changes);
    }
}

