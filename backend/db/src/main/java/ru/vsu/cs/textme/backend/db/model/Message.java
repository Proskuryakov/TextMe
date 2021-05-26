package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class Message {
    private Integer id;
    private String content;
    private Date dateCreate;
    private Date dateUpdate;
    private MessageStatus status;

    public boolean canUpdate() {
        var zonedDate = ZonedDateTime
                .ofInstant(dateCreate.toInstant(), ZoneId.of("Europe/Paris"))
                .plusDays(1);
        var timeoutDate = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
        return zonedDate.isBefore(timeoutDate);
    }
}
