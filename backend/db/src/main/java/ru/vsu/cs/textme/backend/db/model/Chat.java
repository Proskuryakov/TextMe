package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.util.List;

@Data
public class Chat {
    private ChatProfileInfo profile;
    private List<ChatMessage> messages;
}
