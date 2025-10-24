package com.matkon.gamelog.common.exception;

import com.matkon.gamelog.domain.game.exception.GameAlreadyExistException;
import com.matkon.gamelog.domain.game.exception.GameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GameAlreadyExistException.class)
    public ResponseEntity<ApiException> handleGameAlreadyExistException(GameAlreadyExistException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiException(exception.getMessage()));
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiException> handleGameNotFoundException(GameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiException(exception.getMessage()));
    }
}
