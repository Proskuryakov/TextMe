package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;

import static ru.vsu.cs.textme.backend.db.model.MessageError.MESSAGE_NOT_FOUND;
import static ru.vsu.cs.textme.backend.db.model.MessageError.TIMEOUT;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.*;

@Service
public class DirectService {
    private final DirectMapper directMapper;
    private final UserMapper userMapper;

    public DirectService(DirectMapper directMapper, UserMapper userMapper) {
        this.directMapper = directMapper;
        this.userMapper = userMapper;
    }

    public DirectMessage send(String from, String to, String message) {
        var err = getError(from, to);
        if (err != null) throw new DirectException(err, to);
        return directMapper.save(from, to, message);
    }


    public DirectMessage update(String from, String to, MessageUpdate message) {
        var msg = directMapper.findMessageById(message.getId());
        MessageError err = msg == null ? MESSAGE_NOT_FOUND :
                (err = getError(from, to)) != null ? err :
                msg.canUpdate() ? null : TIMEOUT;
        if (err != null) throw new DirectException(err, to);

        return directMapper.update(message.getContent(), message.getId());
    }

    public MessageError getError(String f, String t) {
        User from = userMapper.findUserByNickname(f);
        User to = userMapper.findUserByNickname(t);
        if (to == null) return MessageError.ADDRESS_NOT_FOUND;
        Integer fId = from.getId(), tId = to.getId();
        Integer block = userMapper.isBlocked(fId, tId);
        if (fId.equals(block))
            return MessageError.FROM_BLOCKED;
        else if (tId.equals(block))
            return MessageError.TO_BLOCKED;
        return null;
    }

    public boolean deleteBy(String from, String to, Integer id) {
        var message = directMapper.findDirectMessageById(id);
        return message != null && (
                message.getFrom().equals(from) &&
                message.getTo().equals(to) &&
                directMapper.setStatusById(id, DELETED.ordinal()) != null);
    }

    public boolean readBy(String from, String to, Integer id) {
        var message = directMapper.findDirectMessageById(id);
        return message != null && (
                message.getFrom().equals(from) &&
                        message.getTo().equals(to) &&
                        directMapper.setStatusById(id, READ.ordinal()) != null);
    }
}
