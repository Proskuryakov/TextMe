package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
public class ReportRequest {
    @NotNull
    private Integer card;
    @Size(max = 2048)
    private String message;
}
