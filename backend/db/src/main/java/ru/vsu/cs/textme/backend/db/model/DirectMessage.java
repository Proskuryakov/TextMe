package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class DirectMessage {
    private String from;
    private String to;
    private Message message;
}
