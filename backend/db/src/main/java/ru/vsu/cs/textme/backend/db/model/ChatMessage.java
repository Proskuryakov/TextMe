package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class ChatMessage {
    private ChatMembers chat;
    private String user;
    private Message message;

    public boolean isRecipient(String name) {
        return !user.equals(name) && chat.isMember(name);
    }
}
