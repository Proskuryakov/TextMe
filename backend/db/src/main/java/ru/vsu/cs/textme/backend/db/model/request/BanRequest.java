package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BanRequest {
    private Integer cardId;
    private Timestamp expired;
}
