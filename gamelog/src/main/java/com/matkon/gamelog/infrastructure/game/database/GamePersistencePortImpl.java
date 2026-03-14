package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.common.exception.ItemAlreadyExistsException;
import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.model.dashboard.DashboardDto;
import com.matkon.gamelog.domain.game.model.dashboard.GameStatsDto;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.ports.out.GamePersistencePort;
import com.matkon.gamelog.domain.game.sync.GameSyncStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
class GamePersistencePortImpl implements GamePersistencePort {

    private final GameJpaRepository gameJpaRepository;
    private final GameMapper gameMapper;
    private final GameInfoPort gameInfoPort;
    private final GameSyncStrategy syncStrategy;

    @Override
    public Page<Game> getGames(int page, int size, String status, String searchTerm) {
        log.info("Getting games with status: {}, searchTerm: {}", status, searchTerm);
        Pageable pageable = PageRequest.of(page, size);

        GameStatus dbStatus;
        if (status == null || "ALL".equals(status) || status.trim().isEmpty()) {
            dbStatus = null;
        } else {
            try {
                dbStatus = GameStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid status: {}", status);
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        String dbSearchTerm = (searchTerm == null || searchTerm.trim().isEmpty()) ? null : searchTerm;

        Page<GameEntity> games = gameJpaRepository.findGamesByStatus(dbStatus, dbSearchTerm, pageable);
        return games.map(gameMapper::mapGameEntityToGame);
    }

    @Override
    public Game saveGame(Long rawgId, GameStatus gameStatus) {
        log.info("Saving game with rawgId: {}", rawgId);
        gameJpaRepository.findByRawgId(rawgId)
                .ifPresent(game -> {
                    log.error("Game already exists with rawgId: {}", rawgId);
                    throw new ItemAlreadyExistsException(rawgId);
                });

        Game game = Optional.ofNullable(gameInfoPort.getGameDetails(rawgId))
                .orElseThrow(() -> new ItemNotFoundException("Game with ID '%s' not found in external API".formatted(rawgId)));

        game.setStatus(gameStatus);
        GameEntity savedGameEntity = gameJpaRepository.save(gameMapper.mapGameToGameEntity(game));

        log.info("Game id={} title={} saved", savedGameEntity.getId(), savedGameEntity.getTitle());
        return gameMapper.mapGameEntityToGame(savedGameEntity);
    }

    @Override
    public void deleteGame(Long gameId) {
        log.info("Deleting game with id: {}", gameId);
        if (!gameJpaRepository.existsById(gameId)) {
            log.error("Game with id: {} not found in the database", gameId);
            throw new ItemNotFoundException("Game with ID '%s' not found in the database".formatted(gameId));
        }
        gameJpaRepository.deleteById(gameId);
    }

    @Override
    @Transactional
    public Game updateGame(Long id, GameUpdate updateRequest) {
        log.info("Updating game with id: {}", id);
        GameEntity existingGameEntity = gameJpaRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Game not found with id: " + id));

        Optional.ofNullable(updateRequest.getPlatform()).ifPresent(existingGameEntity::setPlatform);
        Optional.ofNullable(updateRequest.getStatus()).ifPresent(existingGameEntity::setStatus);
        Optional.ofNullable(updateRequest.getRating()).ifPresent(existingGameEntity::setRating);
        Optional.ofNullable(updateRequest.getNotes()).ifPresent(existingGameEntity::setNotes);
        Optional.ofNullable(updateRequest.getCompletedAt()).ifPresent(existingGameEntity::setCompletedAt);
        Optional.ofNullable(updateRequest.getFavourite()).ifPresent(existingGameEntity::setFavourite);

        return gameMapper.mapGameEntityToGame(existingGameEntity);
    }

    @Override
    public SyncResult syncGamesByStatus(GameStatus status) {
        log.info("Syncing games with status: {}", status);
        List<GameEntity> games = gameJpaRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == status)
                .toList();
        return syncStrategy.sync(games.stream().map(gameMapper::mapGameEntityToGame).toList());
    }

    @Override
    public DashboardDto getDashboard() {
        log.info("Getting dashboard");
        GameStatsDto stats = new GameStatsDto(
                gameJpaRepository.countTotal(),
                gameJpaRepository.countByStatus(GameStatus.WISHLIST),
                gameJpaRepository.countByStatus(GameStatus.PLAYING),
                gameJpaRepository.countByStatus(GameStatus.COMPLETED),
                gameJpaRepository.countByStatus(GameStatus.DROPPED),
                gameJpaRepository.countByStatus(GameStatus.ONLINE),
                gameJpaRepository.averageRating()
        );

        return new DashboardDto(
                stats,
                gameJpaRepository.platformStats(),
                gameJpaRepository.completionsPerYear(),
                gameJpaRepository.recentlyCompleted(PageRequest.of(0, 5))
        );
    }

}