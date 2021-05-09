package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppRole {
    public static final AppRole USER = new AppRole(0, "USER");
    public static final AppRole MODER = new AppRole(1, "MODER");
    public static final AppRole ADMIN = new AppRole(2, "ADMIN");

    private Integer id;
    private String content;
}
