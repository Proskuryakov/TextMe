package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;
import ru.vsu.cs.textme.backend.db.model.request.NewMessageRequest;
import ru.vsu.cs.textme.backend.services.exception.DirectException;

import java.util.List;

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
    private String toString(List<String> str) {
        return '{' + String.join(",", str) + '}';
    }
    public MessageInfo send(Integer from, NewMessageRequest request) {
        checkAccess(from, request.getRecipient());
        var message = directMapper.save(from, request.getRecipient(), request.getMessage());
        for (var path : request.getImages()) {
            directMapper.saveMessageFile(message.getMessage().getId(), path);
        }
        message.setDestination(DIRECT);
        message.setImages(request.getImages());
        return message;
    }

    public MessageInfo update(Integer from, MessageUpdate update) {
        var message = directMapper.findDirectMessageById(update.getId());
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND);
        checkAccess(from, message.getTo().getId());

        if (message.getMessage().expiredUpdate()) {
            throw new DirectException(TIMEOUT);
        }
        directMapper.update(update.getContent(), update.getId());
        message.setDestination(DIRECT);
        message.getMessage().setContent(update.getContent());
        return message;
    }


    public MessageInfo deleteBy(Integer fromId, Integer msgId) {
        var message = directMapper.findDirectMessageById(msgId);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND);
        if (!message.getFrom().getId().equals(fromId)) {
            throw new DirectException(NOT_PERMS);
        }
        if (directMapper.setStatusById(msgId, DELETED.ordinal()))
            message.getMessage().setStatus(DELETED);
        message.setDestination(DIRECT);
        return message;
    }

    public MessageInfo readBy(Integer fromId, Integer msgId) {
        var message = directMapper.findDirectMessageById(msgId);
        if (message == null)
            throw new DirectException(MESSAGE_NOT_FOUND);
        if (!message.getTo().getId().equals(fromId)) {
            throw new DirectException(NOT_PERMS);
        }
        if (directMapper.setStatusById(msgId, RECIEVED.ordinal())) {
            message.getMessage().setStatus(RECIEVED);
        }
        message.setDestination(DIRECT);
        return message;
    }


    public void checkAccess(Integer fromId, Integer toId) {
        User from = userMapper.findUserById(fromId);
        User to = userMapper.findUserById(toId);
        if (to == null)
            throw new DirectException(MessageError.ADDRESS_NOT_FOUND);
        Integer fId = from.getId(), tId = to.getId();
        Integer block = userMapper.findBlocked(fId, tId);
        if (fId.equals(block))
            throw new DirectException(FROM_BLOCKED);
        else if (tId.equals(block))
            throw new DirectException(TO_BLOCKED);
    }
}
