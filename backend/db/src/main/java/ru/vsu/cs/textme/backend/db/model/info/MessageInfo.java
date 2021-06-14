package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.Message;

import java.util.Collections;
import java.util.List;

@Data
public class MessageInfo {
    private Info from;
    private Info to;
    private Message message;
    private DestinationType destination = DestinationType.DIRECT;
    private List<String> images = Collections.emptyList();

    public enum DestinationType {
        CHAT, DIRECT
    }
}
