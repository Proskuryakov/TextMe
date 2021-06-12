package ru.vsu.cs.textme.backend.db.model.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.vsu.cs.textme.backend.db.model.Message;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageInfo {
    private Info user;
    private Info chat;
    private Message message;
}
