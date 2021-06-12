package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.services.exception.ChatException;

import static ru.vsu.cs.textme.backend.db.model.ChatRole.ROLE_BLOCKED;
import static ru.vsu.cs.textme.backend.db.model.MessageError.*;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.DELETED;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.READ;

@Service
public class ChatService {
    private final ChatMapper chatMapper;
    private final UserMapper userMapper;

    public ChatService(ChatMapper chatMapper, UserMapper userMapper) {
        this.chatMapper = chatMapper;
        this.userMapper = userMapper;
    }

    public ChatMessage send(Integer chat, String message, String user) {
        var err = getError(user, chat);
        if (err != null) throw new ChatException(err,chat);
        return chatMapper.save(user, chat, message);
    }

    private MessageError getError(String name, Integer c) {
        Chat chat = chatMapper.findChatById(c);
        if (chat == null) return ADDRESS_NOT_FOUND;
        var role = chat.getRole(name);
        return role == null ? ADDRESS_NOT_FOUND : role == ROLE_BLOCKED ? TO_BLOCKED : null;
    }

    public ChatMessage update(Integer id, MessageUpdate update, String user) {
        var msg = chatMapper.findMessageById(update.getId());
        MessageError err = msg == null ? MESSAGE_NOT_FOUND :
                (err = getError(user, id)) != null ? err :
                        msg.canUpdate() ? TIMEOUT : null;
        if (err != null) throw new ChatException(err, id);
        return chatMapper.update(update.getContent(), update.getId());
    }

    public ChatMessage deleteBy(String user, Integer chat, Integer msg) {
        var message = chatMapper.findChatMessageById(chat);
        return message != null && (
                message.getChat().canDeleteMessage(user) &&
                chatMapper.setStatusById(msg, DELETED.ordinal()) != null) ? message : null;
    }

    public ChatMessage readBy(String user, Integer chat, Integer msg) {
        var message = chatMapper.findChatMessageById(chat);
        return message != null && (
                message.isRecipient(user) &&
                chatMapper.setStatusById(msg, READ.ordinal()) != null) ? message : null;
    }

    public Chat create(Integer userId, String name) {
        return chatMapper.createChat(userId, name);
    }

    public void deleteChat(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return;
        for (var member : members) {
            if ( member.isSameId(userId)) {
                if (member.getRole().canDeleteChat())
                    chatMapper.deleteChat(chatId);
                break;
            }
        }
    }

    public void changeName(Integer userId, Integer chatId, String name) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return;
        for (var member : members) {
            if (member.isSameId(userId)) {
                if (member.getRole().canChangeName())
                    chatMapper.setTitle(chatId, name);
                break;
            }
        }
    }

    public boolean canUpdateAvatar(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return false;
        for (var member : members) {
            if (member.isSameId(userId)) {
                return member.getRole().canUpdateAvatar();
            }
        }
        return false;
    }

    public void saveAvatar(Integer chatId, String path) {
        chatMapper.saveAvatarById(chatId, path);
    }
}
