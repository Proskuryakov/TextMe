package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String nickname;
    private String password;
}
