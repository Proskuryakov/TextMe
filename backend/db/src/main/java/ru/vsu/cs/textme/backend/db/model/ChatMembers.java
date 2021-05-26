package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.util.List;

@Data
public class ChatMembers {
    private Integer id;
    private String title;
    private List<UserChatInfo> members;

    public boolean isMember(String name) {
        for (var m : members) {
            if (m.getUser().getNickname().equals(name)) return true;
        }
        return false;
    }

    public ChatRole getRole(String name) {
        for (var m : members) {
            if (m.getUser().getNickname().equals(name)) return m.getRole();
        }
        return null;
    }

    public boolean canDeleteMessage(String name) {
        for (var m : members) {
            if (m.getRole() != ChatRole.ROLE_BLOCKED &&
                    (m.getUser().getNickname().equals(name) || m.getRole().deleteMessages())) return true;
        }
        return false;
    }
}
