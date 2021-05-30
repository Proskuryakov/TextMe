package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {
    @Size(min = 6, max = 64)
    @NotNull
    private String old;
    @Size(min = 6, max = 64)
    @NotNull
    private String change;

    public boolean isSimilar() {
        return old.equals(change);
    }
}
