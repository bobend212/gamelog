package com.matkon.gamelog.domain.game.ports.out;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetails;

import java.util.List;

public interface GameInfoPort {

    List<Game> searchGames(String query);

    Game getGame(Long rawgId);

    GameDetails getGameDetails(Long rawgId);
}
