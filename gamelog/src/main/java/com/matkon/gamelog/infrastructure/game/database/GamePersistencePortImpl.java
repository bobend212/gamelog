package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.domain.game.exception.GameAlreadyExistException;
import com.matkon.gamelog.domain.game.exception.GameNotFoundException;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.model.SyncResult;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.ports.out.GamePersistencePort;
import com.matkon.gamelog.domain.sync.game.GameSyncStrategy;
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
        Pageable pageable = PageRequest.of(page, size);

        GameStatus dbStatus;
        if (status == null || "ALL".equals(status) || status.trim().isEmpty()) {
            dbStatus = null;
        } else {
            try {
                dbStatus = GameStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        String dbSearchTerm = (searchTerm == null || searchTerm.trim().isEmpty()) ? null : searchTerm;

        Page<GameEntity> games = gameJpaRepository.findGamesByStatus(dbStatus, dbSearchTerm, pageable);
        return games.map(gameMapper::mapGameEntityToGame);
    }

    @Override
    public Game saveGame(Long rawgId, GameStatus gameStatus) {
        gameJpaRepository.findByRawgId(rawgId)
                .ifPresent(game -> {
                    throw new GameAlreadyExistException(rawgId);
                });

        Game game = Optional.ofNullable(gameInfoPort.getGameDetails(rawgId))
                .orElseThrow(() -> new GameNotFoundException("Game with ID '%s' not found in external API".formatted(rawgId)));

        game.setStatus(gameStatus);
        GameEntity savedGameEntity = gameJpaRepository.save(gameMapper.mapGameToGameEntity(game));

        log.info("Game saved successfully: id={}", rawgId);
        return gameMapper.mapGameEntityToGame(savedGameEntity);
    }

    @Override
    public void deleteGame(Long gameId) {
        if (!gameJpaRepository.existsById(gameId)) {
            throw new GameNotFoundException("Game with ID '%s' not found in the database".formatted(gameId));
        }
        gameJpaRepository.deleteById(gameId);
    }

    @Override
    @Transactional
    public Game updateGame(Long id, GameUpdate updateRequest) {
        GameEntity existingGameEntity = gameJpaRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + id));

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
        List<GameEntity> games = gameJpaRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == status)
                .toList();

        return syncStrategy.sync(games);
    }
}