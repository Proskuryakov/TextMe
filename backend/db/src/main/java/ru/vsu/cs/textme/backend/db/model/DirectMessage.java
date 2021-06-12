package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.info.Info;

@Data
public class DirectMessage {
    private Info from;
    private Info to;
    private Message message;
}
