package ru.vsu.cs.textme.backend.services.exception;

import ru.vsu.cs.textme.backend.db.model.MessageError;

public class ChatException extends RuntimeException{
    private MessageError error;
    private Integer chat;

    public ChatException(MessageError error, Integer chat) {
        this.error = error;
        this.chat = chat;
    }
}
