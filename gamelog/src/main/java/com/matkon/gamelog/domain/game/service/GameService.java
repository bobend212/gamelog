package com.matkon.gamelog.domain.game.service;

import com.matkon.gamelog.domain.common.sync.SyncResult;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetailsDto;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameSort;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.model.dashboard.DashboardDto;
import com.matkon.gamelog.domain.game.ports.in.DeleteGameUseCase;
import com.matkon.gamelog.domain.game.ports.in.GetGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.SaveGameUseCase;
import com.matkon.gamelog.domain.game.ports.in.SearchGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.SyncGamesUseCase;
import com.matkon.gamelog.domain.game.ports.in.UpdateGameUseCase;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.domain.game.ports.out.GamePersistencePort;
import lombok.AllArgsConstructor;
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
    @Cacheable(value = "games_search_results", key = "#query")
    public List<Game> searchGames(String query) {
        return gameInfoPort.searchGames(query);
    }

    @Override
    public Page<Game> getGames(int page, int size, String status, String searchTerm, GameSort sortBy, String sortDirection) {
        return gamePersistencePort.getGames(page, size, status, searchTerm, sortBy, sortDirection);
    }

    @Override
    public Game saveGame(Long externalId, GameStatus gameStatus) {
        return gamePersistencePort.saveGame(externalId, gameStatus);
    }

    @Override
    public void deleteGame(Long gameId) {
        gamePersistencePort.deleteGame(gameId);
    }

    @Override
    public Game updateGame(Long id, GameUpdate gameUpdate) {
        return gamePersistencePort.updateGame(id, gameUpdate);
    }

    @Override
    public SyncResult syncGamesByStatus(GameStatus gameStatus) {
        return gamePersistencePort.syncGamesByStatus(gameStatus);
    }

    public DashboardDto getDashboard() {
        return gamePersistencePort.getDashboard();
    }

    public GameDetailsDto getGameDetails(Long gameId) {
        return gamePersistencePort.getGameDetails(gameId);
    }
}
