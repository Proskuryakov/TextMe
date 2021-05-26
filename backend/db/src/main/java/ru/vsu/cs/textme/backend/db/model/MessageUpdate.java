package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class MessageUpdate {
    private String content;
    private Integer id;
}
