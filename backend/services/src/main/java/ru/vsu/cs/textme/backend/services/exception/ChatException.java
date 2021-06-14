package ru.vsu.cs.textme.backend.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.vsu.cs.textme.backend.db.model.MessageError;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChatException extends RuntimeException{
    private MessageError error;

    public ChatException(MessageError error) {
        this.error = error;
    }
}
