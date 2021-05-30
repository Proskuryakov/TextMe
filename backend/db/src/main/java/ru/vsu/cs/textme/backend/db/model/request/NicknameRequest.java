package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class NicknameRequest {
    @NotEmpty
    @Size(min=4, max=16)
    private String name;
}
