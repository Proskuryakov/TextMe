package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChatRole {
    ROLE_BLOCKED(3),
    ROLE_MEMBER(0),
    ROLE_MODER(1),
    ROLE_OWNER(2) {
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return true;
        }
    },
    ROLE_ADMIN(4) {
        private boolean canInteract(ChatRole role) {
            return role == ROLE_MODER || role == ROLE_MEMBER || role == ROLE_BLOCKED;
        }
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return canInteract(from) && canInteract(to);
        }
    };
    private final int id;

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

    public int getId() {
        return this.id;
    }

    public boolean canRead() {
        return this != ROLE_BLOCKED;
    }

    public boolean canSend() {
        return canRead();
    }
}
