package ru.vsu.cs.textme.backend.services.exception;

import ru.vsu.cs.textme.backend.db.model.MessageError;

public class DirectException extends RuntimeException {
    private MessageError error;
    private String payload;

    public DirectException(MessageError err, String payload) {
        this.error = err;
        this.payload = payload;
    }
}
