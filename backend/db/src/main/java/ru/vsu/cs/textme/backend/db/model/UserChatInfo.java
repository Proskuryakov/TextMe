package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class UserChatInfo {
    private UserProfileInfo user;
    private ChatRole role;
}
