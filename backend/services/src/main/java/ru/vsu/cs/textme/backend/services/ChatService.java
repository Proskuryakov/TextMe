package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.ChatStatus;
import ru.vsu.cs.textme.backend.db.model.request.NewMessageRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatRoleRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatStatusRequest;
import ru.vsu.cs.textme.backend.services.exception.ChatException;

import java.util.List;
import java.util.function.BiPredicate;

import static ru.vsu.cs.textme.backend.db.model.MessageError.*;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.DELETED;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.READ;

@Service
public class ChatService {
    private final ChatMapper chatMapper;

    public ChatService(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    public ChatMessage send(Integer userId, NewMessageRequest request) {
        var member = getMemberInfo(userId, request.getRecipient());
        if (member == null || !member.canSend())
            throw new ChatException(NOT_PERMS);

        return chatMapper.save(userId, request.getRecipient(), request.getMessage());
    }


    public ChatMessage update(Integer userId, MessageUpdate update) {
        var message = chatMapper.findChatMessageByMessageId(update.getId());
        if (message == null)
            throw new ChatException(MESSAGE_NOT_FOUND);
        if (message.getInfo().getMessage().expiredUpdate())
            throw new ChatException(TIMEOUT);

        var member = getMemberInfo(message.getMembers(), userId);
        if (member == null || !member.canSend())
            throw new ChatException(NOT_PERMS);

        return chatMapper.update(update.getContent(), update.getId());
    }

    public ChatMessage deleteBy(Integer userId, Integer msgId) {
        var message = chatMapper.findChatMessageByMessageId(msgId);
        if (message == null)
            throw new ChatException(MESSAGE_NOT_FOUND);

        var member = getMemberInfo(message.getMembers(), userId);
        if (    member != null &&
                (member.getRole().canDeleteOtherMessages() || member.isSameId(userId)) &&
                chatMapper.setStatusById(message.getInfo().getMessage().getId(), DELETED.ordinal())) {
            message.getInfo().getMessage().setStatus(DELETED);
            return message;
        }

        throw new ChatException(NOT_PERMS);
    }

    public ChatMessage readBy(Integer userId, Integer msgId) {
        var message = chatMapper.findChatMessageByMessageId(msgId);
        if (message == null)
            throw new ChatException(MESSAGE_NOT_FOUND);

        var member = getMemberInfo(message.getMembers(), userId);
        if (member != null && member.canRead() &&
                chatMapper.setStatusById(message.getInfo().getMessage().getId(), READ.ordinal())) {
            message.getInfo().getMessage().setStatus(READ);
            return message;
        }
        throw new ChatException(NOT_PERMS);
    }

    public Chat create(Integer userId, String chatName) {
        return chatMapper.createChat(userId, chatName);
    }

    public void deleteChat(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return;
        var member = getMemberInfo(members, userId);
        if (member != null && member.getRole().canDeleteChat())
            chatMapper.deleteChat(chatId);
    }

    public void changeName(Integer userId, Integer chatId, String name) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return;
        var member = getMemberInfo(members, userId);
        if (member != null && member.getRole().canChangeChatInfo())
            chatMapper.setTitle(chatId, name);
    }

    public void checkAvatarPermissions(Integer userId, Integer chatId) {
        var member = checkAccess(userId, chatId);
        if (!member.getRole().canChangeChatInfo())
            throw new ChatException(NOT_PERMS);
    }

    public void saveAvatar(Integer chatId, String path) {
        chatMapper.saveAvatarById(chatId, path);
    }

    public void setUserRole(Integer userId, Integer chatId, PostChatRoleRequest request) {
        if (checkInteract(userId, chatId, request.getMember(),
                (u, m) -> u.getRole().canChangeRole(m.getRole(), request.getRole()))) {
            chatMapper.saveChatRole(chatId, request.getMember(), request.getRole().getId());
        }
    }

    private final  BiPredicate<ChatMemberInfo, ChatMemberInfo> changeRolePredicate =
            (a, b) -> a.getRole().canChangeStatus(b.getRole());

    public void setUserStatus(Integer userId, Integer chatId, PostChatStatusRequest request) {
        if (checkInteract(userId, chatId, request.getMember(), changeRolePredicate)) {
            chatMapper.saveChatStatus(chatId, request.getMember(), request.getStatus().getId());
        }
    }

    public void joinChat(Integer userId, Integer chatId) {
        var member = getMemberInfo(userId, chatId);
        if (member == null || member.canJoinChat()) {
            chatMapper.saveChatStatus(chatId, userId,ChatStatus.STATUS_MEMBER.getId());
            return;
        }
        throw new ChatException(NOT_PERMS);

    }

    public void leaveChat(Integer userId, Integer chatId) {
        var member = getMemberInfo(userId, chatId);
        if (member == null || member.canLeaveChat()) {
            chatMapper.saveChatStatus(chatId, userId,ChatStatus.STATUS_LEAVE.getId());
            return;
        }
        throw new ChatException(NOT_PERMS);
    }

    public ChatMemberInfo checkAccess(Integer userId, Integer chatId) {
       return checkInteract(userId, chatId);
    }

    private boolean checkInteract(Integer userId, Integer chatId, Integer memberId,
                                  BiPredicate<ChatMemberInfo, ChatMemberInfo> predicate) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null)
            throw new ChatException(ADDRESS_NOT_FOUND);
        var user = getMemberInfo(members, memberId);
        if (user == null)
            throw new ChatException(NOT_PERMS);

        var member = getMemberInfo(members, userId);
        if (member == null) throw new ChatException(ADDRESS_NOT_FOUND);
        return predicate.test(user, member);
    }

    private ChatMemberInfo checkInteract(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null)
            throw  new ChatException(ADDRESS_NOT_FOUND);
        var user = getMemberInfo(members, userId);
        if (user == null)
            throw  new ChatException(NOT_PERMS);

        var member = getMemberInfo(members, userId);
        if (member == null) throw new ChatException(ADDRESS_NOT_FOUND);
        if (member.getStatus() != ChatStatus.STATUS_MEMBER) {
            throw new ChatException(NOT_PERMS);
        }
        return member;
    }

    private ChatMemberInfo getMemberInfo(List<ChatMemberInfo> list, Integer id) {
        for (var member : list) {
            if (member.isSameId(id)) {
                return member;
            }
        }
        return null;
    }

    private ChatMemberInfo getMemberInfo(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) throw  new ChatException(ADDRESS_NOT_FOUND);
        return getMemberInfo(members, userId);
    }

    public Chat getChat(Integer user, Integer chat) {
        return chatMapper.findChatById(chat);
    }
}
