package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.info.Info;

import java.util.List;

@Data
public class Chat{
    private Info info;
    private List<ChatMemberInfo> members;

    public boolean isMember(String name) {
        for (var m : members) {
            if (m.getMember().getName().equals(name)) return true;
        }
        return false;
    }

    public ChatRole getRole(String name) {
        for (var m : members) {
            if (m.getMember().getName().equals(name)) return m.getRole();
        }
        return null;
    }

    public boolean canDeleteMessage(String name) {
        for (var m : members) {
            if (m.getRole() != ChatRole.ROLE_BLOCKED &&
                    (m.getMember().getName().equals(name) || m.getRole().deleteMessages())) return true;
        }
        return false;
    }
}
