package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Message {
    private Integer id;
    private String content;
    private ZonedDateTime dateCreate;
    private ZonedDateTime dateUpdate;
    private MessageStatus status;

    public boolean expiredUpdate() {
        return dateUpdate != null && dateUpdate.isAfter(dateCreate.plusDays(1));
    }
}
