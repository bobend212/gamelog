package com.matkon.gamelog.domain.game.ports.in;

import com.matkon.gamelog.domain.game.model.Game;
import org.springframework.data.domain.Page;

public interface GetGamesUseCase {

    Page<Game> getGames(int page, int size, String status, String searchTerm);
}
