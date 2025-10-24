package com.matkon.gamelog.domain.game.ports.out;

import com.matkon.gamelog.domain.game.model.Game;

import java.util.List;

public interface GameInfoPort {

    List<Game> searchGames(String query);

    Game getGameDetails(Long rawgId);
}
