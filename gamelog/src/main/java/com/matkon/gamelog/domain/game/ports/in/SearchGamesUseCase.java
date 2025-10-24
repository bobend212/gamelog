package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.Game;

import java.util.List;

public interface SearchGamesUseCase {

    List<Game> searchGames(String query);
}
