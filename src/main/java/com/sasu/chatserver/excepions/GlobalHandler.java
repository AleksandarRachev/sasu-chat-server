package com.sasu.chatserver.excepions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler({ElementNotPresentException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleNotFount(Exception e) {
        return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({ElementAlreadyExistsException.class, PasswordsNotMatchingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleBadRequest(Exception e) {
        return ResponseEntity.status(400).body(new ErrorMessage(e.getMessage()));
    }

}
