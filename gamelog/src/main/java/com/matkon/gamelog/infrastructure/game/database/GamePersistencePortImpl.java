package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.api.game.GameMapper;
import com.matkon.gamelog.domain.game.exception.GameAlreadyExistException;
import com.matkon.gamelog.domain.game.exception.GameNotFoundException;
import com.matkon.gamelog.domain.game.model.FieldDifference;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.model.SyncResult;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.ports.out.GamePersistencePort;
import com.matkon.gamelog.domain.sync.SyncUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
class GamePersistencePortImpl implements GamePersistencePort {

    private final GameJpaRepository gameJpaRepository;
    private final GameMapper gameMapper;
    private GameInfoPort gameInfoPort;

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
        Optional<GameEntity> existingGameOpt = gameJpaRepository.findByRawgId(rawgId);

        if (existingGameOpt.isPresent()) {
            throw new GameAlreadyExistException(rawgId);
        }

        try {
            Game game = gameInfoPort.getGameDetails(rawgId);

            if (game != null) {
                game.setStatus(gameStatus);
                GameEntity savedGameEntity = gameJpaRepository.save(gameMapper.mapGameToGameEntity(game));
                return gameMapper.mapGameEntityToGame(savedGameEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error adding game to library");
        }

        throw new GameNotFoundException("Game with following ID '%s' does not exist in the API".formatted(rawgId));
    }

    @Override
    public void deleteGame(Long gameId) {
        boolean exists = gameJpaRepository.existsById(gameId);
        if (!exists) {
            throw new GameNotFoundException("Game not found with ID: " + gameId);
        }
        gameJpaRepository.deleteById(gameId);
    }

    @Override
    @Transactional
    public Game updateGame(Long id, GameUpdate updateRequest) {
        GameEntity existingGameEntity = gameJpaRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + id));

        existingGameEntity.setPlatform(updateRequest.getPlatform());
        existingGameEntity.setStatus(updateRequest.getStatus());
        existingGameEntity.setRating(updateRequest.getRating());
        existingGameEntity.setNotes(updateRequest.getNotes());
        existingGameEntity.setCompletedAt(updateRequest.getCompletedAt());
        existingGameEntity.setUpdatedAt(LocalDateTime.now());
        existingGameEntity.setFavourite(updateRequest.isFavourite());

        return gameMapper.mapGameEntityToGame(existingGameEntity);
    }

    @Override
    public SyncResult syncGamesByStatus(GameStatus gameStatus) {
        List<GameEntity> gameEntities = gameJpaRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == gameStatus)
                .toList();

        int updatedCount = 0;
        List<FieldDifference> changes = new ArrayList<>();

        for (GameEntity localGameEntity : gameEntities) {
            Game latestData = gameInfoPort.getGameDetails(localGameEntity.getRawgId());
            if (latestData == null) continue;

            boolean changed = false;

            String gameTitle = localGameEntity.getTitle();
            if (SyncUtils.areDatesDifferent(localGameEntity.getReleaseDate(), latestData.getReleaseDate())) {
                changes.add(FieldDifference.builder()
                        .title(gameTitle)
                        .fieldName("ReleaseDate")
                        .oldValue(String.valueOf(localGameEntity.getReleaseDate()))
                        .newValue(String.valueOf(latestData.getReleaseDate()))
                        .build());
                localGameEntity.setReleaseDate(latestData.getReleaseDate());
                changed = true;
            }

            if (SyncUtils.areStringsDifferent(gameTitle, latestData.getTitle())) {
                changes.add(FieldDifference.builder()
                        .title(gameTitle)
                        .fieldName("Title")
                        .oldValue(gameTitle)
                        .newValue(latestData.getTitle())
                        .build());
                localGameEntity.setTitle(latestData.getTitle());
                changed = true;
            }

            if (SyncUtils.areStringsDifferent(localGameEntity.getImageUrl(), latestData.getImageUrl())) {
                changes.add(FieldDifference.builder()
                        .title(gameTitle)
                        .fieldName("ImageUrl")
                        .oldValue(localGameEntity.getImageUrl())
                        .newValue(latestData.getImageUrl())
                        .build());
                localGameEntity.setImageUrl(latestData.getImageUrl());
                changed = true;
            }

            if (changed) {
                gameJpaRepository.save(localGameEntity);
                updatedCount++;
            }
        }

        return new SyncResult(gameEntities.size(), updatedCount, changes);
    }
}