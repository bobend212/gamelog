package com.matkon.gamelog.common.exception;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class ApiException {
    private final String message;
    private final ZonedDateTime timestamp;

    public ApiException(String message) {
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }
}
