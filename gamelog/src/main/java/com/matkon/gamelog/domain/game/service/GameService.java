package com.matkon.gamelog.domain.game.service;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.game.ports.in.DeleteGameUseCase;
import com.matkon.gamelog.domain.game.ports.in.GetGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.SaveGameUseCase;
import com.matkon.gamelog.domain.game.ports.in.SearchGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.SyncGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.UpdateGameUseCase;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.ports.out.GamePersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GameService
        implements SearchGamesUseCase, GetGamesUseCase, SaveGameUseCase, DeleteGameUseCase, UpdateGameUseCase, SyncGamesUseCase {

    private GamePersistencePort gamePersistencePort;
    private GameInfoPort gameInfoPort;

    @Override
    public List<Game> searchGames(String query) {
        return gameInfoPort.searchGames(query);
    }

    @Override
    @Cacheable("games")
    public Page<Game> getGames(int page, int size, String status, String searchTerm) {
        return gamePersistencePort.getGames(page, size, status, searchTerm);
    }

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public Game saveGame(Long rawgId, GameStatus gameStatus) {
        return gamePersistencePort.saveGame(rawgId, gameStatus);
    }

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public void deleteGame(Long gameId) {
        gamePersistencePort.deleteGame(gameId);
    }

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public Game updateGame(Long id, GameUpdate gameUpdate) {
        return gamePersistencePort.updateGame(id, gameUpdate);
    }

    @Override
    @CacheEvict(value = "games", allEntries = true)
    public SyncResult syncGamesByStatus(GameStatus gameStatus) {
        return gamePersistencePort.syncGamesByStatus(gameStatus);
    }
}