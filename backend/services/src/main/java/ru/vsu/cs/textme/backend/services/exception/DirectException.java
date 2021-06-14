package ru.vsu.cs.textme.backend.services.exception;

import ru.vsu.cs.textme.backend.db.model.MessageError;

public class DirectException extends RuntimeException {
    private MessageError error;

    public DirectException(MessageError err) {
        this.error = err;
    }
}
