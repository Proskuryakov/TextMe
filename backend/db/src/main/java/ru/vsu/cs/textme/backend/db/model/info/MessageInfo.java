package ru.vsu.cs.textme.backend.db.model.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.Message;
import ru.vsu.cs.textme.backend.db.model.info.Info;

import javax.print.attribute.standard.Destination;

@Data
@AllArgsConstructor
public class MessageInfo {
    private Info from;
    private Info to;
    private Message message;
    private DestinationType destination;

    public enum DestinationType {
        CHAT, DIRECT
    }
}
