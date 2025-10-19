package com.matkon.gamelog.api.common.exception;

import com.matkon.gamelog.domain.game.exception.GameAlreadyExistException;
import com.matkon.gamelog.domain.game.exception.GameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {GameAlreadyExistException.class})
    public ResponseEntity<Object> handleApiRequestException(GameAlreadyExistException e) {
        HttpStatus conflict = HttpStatus.CONFLICT;
        ApiException apiException = new ApiException(conflict, e.getMessage());
        return new ResponseEntity<>(apiException, conflict);
    }

    @ExceptionHandler(value = {GameNotFoundException.class})
    public ResponseEntity<Object> handleApiRequestException(GameNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(notFound, e.getMessage());
        return new ResponseEntity<>(apiException, notFound);
    }
}
