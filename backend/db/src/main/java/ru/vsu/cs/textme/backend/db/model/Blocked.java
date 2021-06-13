package ru.vsu.cs.textme.backend.db.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter
public class Blocked {
    private ZonedDateTime from;
    private ZonedDateTime to;
}
