package ru.vsu.cs.textme.backend.db.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public enum ChatRole {
    ROLE_OWNER(0) {
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return to != this && from != this;
        }

        @Override
        public boolean canChangeStatus(ChatRole changed) {
            return changed != this;
        }
    },

    ROLE_ADMIN(1) {
        private boolean canInteract(ChatRole role) {
            return role == ROLE_MODER || role == null;
        }
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return canInteract(from) && canInteract(to);
        }
        @Override
        public boolean canChangeStatus(ChatRole changed) {
            return changed == null || changed == ROLE_MODER;
        }
    },
    ROLE_MODER(2) {
        @Override
        public boolean canChangeRole(ChatRole from, ChatRole to) {
            return false;
        }

        @Override
        public boolean canChangeStatus(ChatRole changed) {
            return changed == null;
        }
    };

    private final int id;

    public boolean canDeleteChat() {
        return this == ROLE_OWNER;
    }

    public boolean canChangeChatInfo() {
        return this == ROLE_ADMIN || this == ROLE_OWNER;
    }

    public boolean canDeleteOtherMessages() {
        return this == ROLE_MODER || this == ROLE_ADMIN || this == ROLE_OWNER ;
    }

    public abstract boolean canChangeRole(ChatRole from, ChatRole to);
    public abstract boolean canChangeStatus(ChatRole changed);
}
