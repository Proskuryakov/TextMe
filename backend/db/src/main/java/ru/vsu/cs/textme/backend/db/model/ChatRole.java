package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChatRole {
    ROLE_MEMBER(false),
    ROLE_MODER(true),
    ROLE_OWNER(true),
    ROLE_BLOCKED(false);
    private final boolean deleteMessages;

    public boolean deleteMessages() {
        return deleteMessages;
    }
}
