package com.matkon.gamelog.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
public class ApiException {
    private final HttpStatus httpStatus;
    private final String message;
    private final ZonedDateTime timestamp;

    public ApiException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timestamp = ZonedDateTime.now();
    }
}
