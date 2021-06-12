package ru.vsu.cs.textme.backend.services.exception;

import ru.vsu.cs.textme.backend.db.model.MessageError;

public class DirectException extends RuntimeException {
    private MessageError error;
    private String recipient;

    public DirectException(MessageError err, String recipient) {
        this.error = err;
        this.recipient = recipient;
    }
}
