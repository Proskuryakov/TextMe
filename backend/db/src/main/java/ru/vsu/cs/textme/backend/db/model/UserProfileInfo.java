package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class UserProfileInfo {
    private Integer id;
    private String nickname;
    private String imageUrl;
    private Card card;
}
