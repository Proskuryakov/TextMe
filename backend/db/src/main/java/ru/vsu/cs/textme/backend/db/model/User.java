package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Integer id;
    private String nickname;
    private String password;
    private List<AppRole> roles;
    private Blocked blocked;

    public boolean hasRole(AppRole role) {
        return roles.stream().anyMatch(r -> r == role);
    }
}
