package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NewDirectMessageRequest {
    private String recipient;
    private String message;
}
