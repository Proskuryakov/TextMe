package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;
import ru.vsu.cs.textme.backend.db.model.request.NewDirectMessageRequest;
import ru.vsu.cs.textme.backend.services.exception.DirectException;

import static ru.vsu.cs.textme.backend.db.model.MessageError.*;
import static ru.vsu.cs.textme.backend.db.model.info.MessageInfo.DestinationType.DIRECT;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.*;

@Service
public class DirectService {
    private final DirectMapper directMapper;
    private final UserMapper userMapper;

    public DirectService(DirectMapper directMapper, UserMapper userMapper) {
        this.directMapper = directMapper;
        this.userMapper = userMapper;
    }

    public MessageInfo send(String from, NewDirectMessageRequest request) {
        var err = getAccessError(from, request.getRecipient());
        if (err != null) throw new DirectException(err, request.getRecipient());
        var message = directMapper.save(from, request.getRecipient(), request.getMessage());
        message.setDestination(DIRECT);
        return message;
    }

    public MessageInfo update(String from, MessageUpdate update) {
        var message = directMapper.findDirectMessageById(update.getId());
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND, update.getId().toString());
        var err = getAccessError(from, message.getTo().getName());
        if (err != null) {
            throw new DirectException(err, message.getTo().getName());
        }
        if (!message.getMessage().canUpdate()) {
            throw new DirectException(TIMEOUT,  update.getId().toString());
        }
        directMapper.update(update.getContent(), update.getId());
        message.setDestination(DIRECT);
        message.getMessage().setContent(update.getContent());
        return message;
    }


    public MessageInfo deleteBy(String from, Integer id) {
        var message = directMapper.findDirectMessageById(id);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND, id.toString());
        if (!message.getFrom().getName().equals(from)) {
            throw new DirectException(NOT_PERMS, id.toString());
        }
        if (directMapper.setStatusById(id, DELETED.ordinal()))
            message.getMessage().setStatus(DELETED);
        message.setDestination(DIRECT);
        return message;
    }

    public MessageInfo readBy(String from, Integer id) {
        var message = directMapper.findDirectMessageById(id);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND, id.toString());
        if (!message.getFrom().getName().equals(from)) {
            throw new DirectException(NOT_PERMS, id.toString());
        }
        if (directMapper.setStatusById(id, READ.ordinal())) {
            message.getMessage().setStatus(READ);
        }
        message.setDestination(DIRECT);
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
