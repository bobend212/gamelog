package com.matkon.gamelog.api.game;

import com.matkon.gamelog.domain.game.model.GameDetails;

public record GameDetailsResponse(GameResponse game, GameDetails details) {
}
