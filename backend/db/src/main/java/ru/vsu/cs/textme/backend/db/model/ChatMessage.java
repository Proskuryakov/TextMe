package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;

import java.util.List;

@Data
public class ChatMessage {
    private MessageInfo info;
    private List<ChatMemberInfo> members;
}
