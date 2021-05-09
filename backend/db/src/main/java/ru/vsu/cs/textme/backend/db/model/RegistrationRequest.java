package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {
    @Email
    @NotNull
    private String email;
    @Size(min = 4, max = 16)
    @NotNull
    private String nickname;
    @Size(min = 6, max = 64)
    @NotNull
    private String password;
}
