package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ContentRequest {
    @Size(max = 256)
    private  String content;
}
