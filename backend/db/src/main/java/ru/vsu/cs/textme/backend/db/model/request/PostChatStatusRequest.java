package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.vsu.cs.textme.backend.db.model.ChatStatus;

@Getter @Setter
public class PostChatStatusRequest {
    public Integer member;
    public ChatStatus status;
}
