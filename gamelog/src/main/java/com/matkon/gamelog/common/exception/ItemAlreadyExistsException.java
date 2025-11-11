package com.matkon.gamelog.common.exception;

public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(Long rawgId) {
        super("Game with RAWG ID " + rawgId + " already exists in the database.");
    }
}
