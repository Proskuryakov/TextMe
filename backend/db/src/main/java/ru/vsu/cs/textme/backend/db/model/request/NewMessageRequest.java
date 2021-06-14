package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class NewMessageRequest {
    private Integer recipient;
    private String message;
    private List<String> images;
}
