package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

@Data
public class PermissionRequest {
    private Integer user;
    private Integer role;
    private boolean permitted;
}
