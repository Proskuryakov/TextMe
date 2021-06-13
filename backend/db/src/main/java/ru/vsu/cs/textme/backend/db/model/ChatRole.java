package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChatRole {
    ROLE_OWNER(0) {
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return true;
        }
    },
    ROLE_ADMIN(1) {
        private boolean canInteract(ChatRole role) {
            return role == ROLE_MODER || role == ROLE_MEMBER || role == ROLE_BLOCKED;
        }
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return canInteract(from) && canInteract(to);
        }
    },
    ROLE_MODER(2),
    ROLE_MEMBER(3),
    ROLE_LEAVE(4),
    ROLE_BLOCKED(5);

    private final int id;

    public int getId() {
        return this.id;
    }

    public boolean canDeleteOtherMessages() {
        return this == ROLE_MODER || this == ROLE_ADMIN || this == ROLE_OWNER ;
    }

    public boolean canDeleteChat() {
        return this == ROLE_OWNER;
    }

    public boolean canChangeName() {
        return this == ROLE_ADMIN || this == ROLE_OWNER;
    }

    public boolean canUpdateAvatar() {
        return this == ROLE_ADMIN || this == ROLE_OWNER;
    }

    public boolean canChangeCard() {
        return this == ROLE_ADMIN || this == ROLE_OWNER;
    }

    public boolean canChangeRole(ChatRole from, ChatRole to) {
        return false;
    }

    public boolean canRead() {
        return this != ROLE_BLOCKED && this != ROLE_LEAVE;
    }

    public boolean canSend() {
        return this != ROLE_BLOCKED && this != ROLE_LEAVE;
    }

    public boolean canJoinChat() {
        return this == ROLE_LEAVE;
    }

    public boolean canLeaveChat() {
        return this != ROLE_LEAVE && this != ROLE_BLOCKED && this != ROLE_OWNER;
    }
}
