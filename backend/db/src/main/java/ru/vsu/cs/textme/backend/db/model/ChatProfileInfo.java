package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class ChatProfileInfo {
    private Integer id;
    private String title;
    private String imageUrl;
    private Card card;
}
