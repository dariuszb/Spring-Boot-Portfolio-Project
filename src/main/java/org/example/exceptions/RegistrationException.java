package org.example.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleRegisterUserException(RegistrationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
