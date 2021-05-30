package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.info.Info;

@Data
public class ChatMessage {
    private Chat chat;
    private Info user;
    private Message message;

    public boolean isRecipient(String name) {
        return !user.getName().equals(name) && chat.isMember(name);
    }
}
