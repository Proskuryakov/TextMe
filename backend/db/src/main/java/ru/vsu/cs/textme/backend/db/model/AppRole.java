package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class AppRole {
    public enum Type {
        USER, MODER, ADMIN
    }
    private Integer id;
    private String content;
}
