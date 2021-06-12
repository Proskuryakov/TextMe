package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NewChatMessageRequest {
    private Integer chatId;
    private String message;
}
