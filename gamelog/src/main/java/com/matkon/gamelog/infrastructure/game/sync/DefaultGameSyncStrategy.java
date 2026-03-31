package com.matkon.gamelog.infrastructure.game.sync;

import com.matkon.gamelog.domain.common.sync.FieldDifference;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.sync.FieldSyncStrategy;
import com.matkon.gamelog.domain.game.sync.GameSyncStrategy;
import com.matkon.gamelog.infrastructure.game.database.GameJpaRepository;
import com.matkon.gamelog.infrastructure.game.database.GameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DefaultGameSyncStrategy implements GameSyncStrategy {

    private final GameInfoPort gameInfoPort;
    private final GameJpaRepository gameJpaRepository;
    private final List<FieldSyncStrategy> fieldSyncStrategies;
    private final GameMapper gameMapper;

    public DefaultGameSyncStrategy(GameInfoPort gameInfoPort, GameJpaRepository repository, GameMapper gameMapper) {
        this.gameInfoPort = gameInfoPort;
        this.gameJpaRepository = repository;
        this.gameMapper = gameMapper;
        this.fieldSyncStrategies = List.of(
                new ReleaseDateSyncStrategy(),
                new TitleSyncStrategy(),
                new ImageUrlSyncStrategy()
        );
    }

    @Override
    public SyncResult sync(List<Game> gameEntities) {
        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        for (Game localGame : gameEntities) {
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
                log.info("Updating game: {}", localGame.getTitle());
                gameJpaRepository.save(gameMapper.mapGameToGameEntity(localGame));
                updatedCount++;
            }
        }

        log.info("Syncing games completed: updated={}", changes.size());
        return new SyncResult(gameEntities.size(), updatedCount, changes);
    }
}

