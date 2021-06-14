package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.Message;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageInfo {
    private Info from;
    private Info to;
    private Message message;
    private DestinationType destination;
    private List<String> images;

    public MessageInfo(Info from, Info to, Message message, DestinationType destination) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.destination = destination;
    }

    public void addImage(String path) {
        if (images == null) images = new ArrayList<>();
        images.add(path);
    }

    public enum DestinationType {
        CHAT, DIRECT
    }
}
