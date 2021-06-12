package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.vsu.cs.textme.backend.db.model.ChatRole;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.info.Info;

@Data
public class ChatMemberInfo {
    private Info member;
    private ChatRole role;

    public boolean isSameId(Integer userId) {
        return member.getId().equals(userId);
    }
}
