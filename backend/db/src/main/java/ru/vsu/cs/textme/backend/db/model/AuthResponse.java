package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
}
