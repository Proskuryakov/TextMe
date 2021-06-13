package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.request.NewDirectMessageRequest;
import ru.vsu.cs.textme.backend.services.exception.DirectException;

import static ru.vsu.cs.textme.backend.db.model.MessageError.*;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.*;

@Service
public class DirectService {
    private final DirectMapper directMapper;
    private final UserMapper userMapper;

    public DirectService(DirectMapper directMapper, UserMapper userMapper) {
        this.directMapper = directMapper;
        this.userMapper = userMapper;
    }

    public DirectMessage send(String from, NewDirectMessageRequest request) {
        var err = getAccessError(from, request.getRecipient());
        if (err != null) throw new DirectException(err, request.getRecipient());
        return directMapper.save(from, request.getRecipient(), request.getMessage());
    }

    public DirectMessage update(String from, MessageUpdate message) {
        var msg = directMapper.findDirectMessageById(message.getId());
        if (msg == null)
            throw new DirectException(MESSAGE_NOT_FOUND, message.getId().toString());
        var err = getAccessError(from, msg.getTo().getName());
        if (err != null) {
            throw new DirectException(err, msg.getTo().getName());
        }
        if (!msg.getMessage().canUpdate()) {
            throw new DirectException(TIMEOUT,  message.getId().toString());
        }
        return directMapper.update(message.getContent(), message.getId());
    }


    public DirectMessage deleteBy(String from, Integer id) {
        var message = directMapper.findDirectMessageById(id);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND, id.toString());
        if (!message.getFrom().getName().equals(from)) {
            throw new DirectException(NOT_PERMS, id.toString());
        }
        if (directMapper.setStatusById(id, DELETED.ordinal()))
            message.getMessage().setStatus(DELETED);
        return message;
    }

    public DirectMessage readBy(String from,Integer id) {
        var message = directMapper.findDirectMessageById(id);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND, id.toString());
        if (!message.getFrom().getName().equals(from)) {
            throw new DirectException(NOT_PERMS, id.toString());
        }
        if (directMapper.setStatusById(id, READ.ordinal())) {
            message.getMessage().setStatus(READ);
        }
        return message;
    }


    public MessageError getAccessError(String f, String t) {
        User from = userMapper.findUserByNickname(f);
        User to = userMapper.findUserByNickname(t);
        if (to == null) return MessageError.ADDRESS_NOT_FOUND;
        Integer fId = from.getId(), tId = to.getId();
        Integer block = userMapper.findBlocked(fId, tId);
        if (fId.equals(block))
            return MessageError.FROM_BLOCKED;
        else if (tId.equals(block))
            return MessageError.TO_BLOCKED;
        return null;
    }
}
