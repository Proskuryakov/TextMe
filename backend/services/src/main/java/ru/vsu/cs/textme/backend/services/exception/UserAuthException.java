package ru.vsu.cs.textme.backend.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAuthException extends RuntimeException {
    public UserAuthException(String message) {
        super(message);
    }
}
