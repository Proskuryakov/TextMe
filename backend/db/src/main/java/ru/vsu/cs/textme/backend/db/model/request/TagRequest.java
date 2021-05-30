package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class TagRequest {
    @NotEmpty
    @Size(max = 64)
    private String tag;
}
