package com.matkon.gamelog.domain.game.ports.out;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.common.sync.SyncResult;
import org.springframework.data.domain.Page;

public interface GamePersistencePort {

    Page<Game> getGames(int page, int size, String status, String searchTerm);

    Game saveGame(Long rawgId, GameStatus gameStatus);

    void deleteGame(Long gameId);

    Game updateGame(Long id, GameUpdate updateRequest);

    SyncResult syncGamesByStatus(GameStatus gameStatus);
}
