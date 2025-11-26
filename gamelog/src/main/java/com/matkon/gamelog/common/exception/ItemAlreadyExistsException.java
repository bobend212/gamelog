package com.matkon.gamelog.common.exception;

public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(Long id) {
        super("Item with external id: " + id + " already exists in the database.");
    }
}
