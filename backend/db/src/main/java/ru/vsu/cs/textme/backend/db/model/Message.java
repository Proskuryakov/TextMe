package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
public class Message {
    private Integer id;
    private String content;
    private Timestamp dateCreate;
    private Timestamp dateUpdate;
    private MessageStatus status;

    public boolean expiredUpdate() {
        var crt = ZonedDateTime.ofInstant(dateCreate.toInstant(), ZoneOffset.UTC);
        return ZonedDateTime.now(ZoneOffset.UTC).isAfter(crt.plusDays(1));
    }
}
