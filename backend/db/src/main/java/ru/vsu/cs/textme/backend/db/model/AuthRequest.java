package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AuthRequest {
    @Email
    @Nullable
    private String email;
    @Size(min = 4, max = 16)
    @Nullable
    private String nickname;
    @Size(min = 6, max = 64)
    @NotNull
    private String password;
}
