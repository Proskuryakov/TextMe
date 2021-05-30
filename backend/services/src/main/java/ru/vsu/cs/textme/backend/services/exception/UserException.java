package ru.vsu.cs.textme.backend.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
