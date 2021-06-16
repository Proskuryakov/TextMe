package ru.vsu.cs.textme.backend.db.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Getter @Setter
public class Blocked {
    private Timestamp from;
    private Timestamp to;
}
