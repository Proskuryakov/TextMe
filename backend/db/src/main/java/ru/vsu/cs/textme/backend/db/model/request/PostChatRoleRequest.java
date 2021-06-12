package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.vsu.cs.textme.backend.db.model.ChatRole;

@Getter @Setter
public class PostChatRoleRequest {
    private Integer member;
    private ChatRole role;
}
