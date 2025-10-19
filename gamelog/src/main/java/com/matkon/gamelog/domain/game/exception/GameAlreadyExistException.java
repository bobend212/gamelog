package com.matkon.gamelog.domain.game.exception;

public class GameAlreadyExistException extends RuntimeException {
    public GameAlreadyExistException(Long rawgId) {
        super("Game with RAWG ID " + rawgId + " already exists in the database.");
    }
}
