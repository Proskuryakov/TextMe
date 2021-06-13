package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.ChatRole;

@Data
public class ChatMemberInfo {
    private Info member;
    private ChatRole role;
    private ChatStatus status;

    public boolean isSameId(Integer userId) {
        return member.getId().equals(userId);
    }

    public boolean isSameNickname(String name) {
        return member.getName().equals(name);
    }

    public boolean canLeaveChat() {
        return status == ChatStatus.STATUS_MEMBER;
    }

    public boolean canJoinChat() {
        return status == ChatStatus.STATUS_LEAVE;
    }

    public boolean canSend() {
        return status == ChatStatus.STATUS_MEMBER;
    }

    public boolean canRead() {
        return status == ChatStatus.STATUS_MEMBER;
    }
}
