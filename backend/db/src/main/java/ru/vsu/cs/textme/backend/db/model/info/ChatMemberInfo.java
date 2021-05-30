package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.vsu.cs.textme.backend.db.model.ChatRole;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.info.Info;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatMemberInfo extends Info {
    private ChatRole role;
}
