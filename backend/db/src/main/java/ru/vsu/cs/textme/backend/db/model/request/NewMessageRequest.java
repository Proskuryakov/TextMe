package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class NewMessageRequest {
    @NotNull
    private Integer recipient;
    @NotBlank
    private String message;
    private List<String> images = Collections.emptyList();
}
