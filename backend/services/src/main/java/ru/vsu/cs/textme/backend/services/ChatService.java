package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.request.NewChatMessageRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatRoleRequest;
import ru.vsu.cs.textme.backend.services.exception.ChatException;

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

    public ChatMessage send(NewChatMessageRequest request, String user) {
        var members = chatMapper.findChatMembers(request.getChatId());
        if (members == null) throw new ChatException(ADDRESS_NOT_FOUND, request.getChatId());

        for (var member : members) {
            if (member.isSameNickname(user)) {
                if (member.getRole().canSend())
                    return chatMapper.save(user, request.getChatId(), request.getMessage());
                else
                    break;
            }
        }
        throw new ChatException(NOT_PERMS, request.getChatId());
    }


    public ChatMessage update(MessageUpdate update, String user) {
        var msg = chatMapper.findChatMessageByMessageId(update.getId());
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, update.getId());
        if (!msg.getMessage().canUpdate())
            throw new ChatException(TIMEOUT, update.getId());

        for (var member : msg.getChat().getMembers()) {
            if (member.isSameNickname(user)) {
                if (member.getRole().canSend())
                    return chatMapper.update(update.getContent(), update.getId());
                else
                    break;
            }
        }

        throw new ChatException(NOT_PERMS, msg.getChat().getInfo().getId());
    }

    public ChatMessage deleteBy(String user, Integer msgId) {
        var msg = chatMapper.findChatMessageByMessageId(msgId);
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, msgId);
        for (var member : msg.getChat().getMembers()) {
            if (member.isSameNickname(user)) {
                if (msg.getChat().canDeleteMessage(user)) {
                    if (chatMapper.setStatusById(msg.getMessage().getId(), DELETED.ordinal())) {
                        msg.getMessage().setStatus(DELETED);
                        return msg;
                    }
                }
                break;
            }
        }
        throw new ChatException(NOT_PERMS, msg.getChat().getInfo().getId());
    }

    public ChatMessage readBy(String user, Integer msgId) {
        var msg = chatMapper.findChatMessageByMessageId(msgId);
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, msgId);
        for (var member : msg.getChat().getMembers()) {
            if (member.isSameNickname(user)) {
                if (member.getRole().canRead()) {
                    if (chatMapper.setStatusById(msg.getMessage().getId(), READ.ordinal())) {
                        msg.getMessage().setStatus(READ);
                        return msg;
                    }
                }
                break;
            }
        }
        throw new ChatException(NOT_PERMS, msg.getChat().getInfo().getId());
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

    public void setUserRole(Integer userId, Integer chatId, PostChatRoleRequest request) {
        if (canChangeRole(userId, chatId, request.getMember(), request.getRole())) {
            chatMapper.setChatRole(chatId, request.getMember(), request.getRole().getId());
        }
    }

    public boolean canChangeRole(Integer userId, Integer chatId, Integer memberId, ChatRole to) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return false;
        ChatMemberInfo toChange = null;
        for (var member : members) {
            if (member.isSameId(memberId)) {
                toChange = member;
                break;
            }
        }
        if (toChange == null) return false;

        for (var member : members) {
            if (member.isSameId(userId)) {
                if (member.getRole().canChangeRole(toChange.getRole(), to)) {
                    return true;
                }
            }
        }

        return false;
    }
}
