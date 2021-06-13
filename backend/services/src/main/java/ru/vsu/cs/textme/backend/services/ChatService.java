package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;
import ru.vsu.cs.textme.backend.db.model.info.ChatStatus;
import ru.vsu.cs.textme.backend.db.model.request.NewChatMessageRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatRoleRequest;
import ru.vsu.cs.textme.backend.db.model.request.PostChatStatusRequest;
import ru.vsu.cs.textme.backend.services.exception.ChatException;

import java.util.List;

import static ru.vsu.cs.textme.backend.db.model.MessageError.*;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.DELETED;
import static ru.vsu.cs.textme.backend.db.model.MessageStatus.READ;

@Service
public class ChatService {
    private final ChatMapper chatMapper;

    public ChatService(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    public ChatMessage send(NewChatMessageRequest request, String user) {
        var members = chatMapper.findChatMembers(request.getChatId());
        if (members == null) throw new ChatException(ADDRESS_NOT_FOUND, request.getChatId());

        var member = getMemberInfo(members, user);
        if (member == null || !member.canSend())
            throw new ChatException(NOT_PERMS, request.getChatId());

        return chatMapper.save(user, request.getChatId(), request.getMessage());
    }


    public ChatMessage update(MessageUpdate update, String user) {
        var msg = chatMapper.findChatMessageByMessageId(update.getId());
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, update.getId());
        if (!msg.getMessage().canUpdate())
            throw new ChatException(TIMEOUT, update.getId());

        var member = getMemberInfo(msg.getChat().getMembers(), user);
        if (member == null || !member.canSend())
            throw new ChatException(NOT_PERMS, msg.getChat().getInfo().getId());

        return chatMapper.update(update.getContent(), update.getId());
    }

    public ChatMessage deleteBy(String user, Integer msgId) {
        var msg = chatMapper.findChatMessageByMessageId(msgId);
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, msgId);
        var member = getMemberInfo(msg.getChat().getMembers(), user);
        if (member != null && msg.getChat().canDeleteMessage(user)) {
            if (chatMapper.setStatusById(msg.getMessage().getId(), DELETED.ordinal())) {
                msg.getMessage().setStatus(DELETED);
                return msg;
            }
        }

        throw new ChatException(NOT_PERMS, msg.getChat().getInfo().getId());
    }

    public ChatMessage readBy(String user, Integer msgId) {
        var msg = chatMapper.findChatMessageByMessageId(msgId);
        if (msg == null)
            throw new ChatException(MESSAGE_NOT_FOUND, msgId);

        var member = getMemberInfo(msg.getChat().getMembers(), user);
        if (member != null && member.canRead()) {
            if (chatMapper.setStatusById(msg.getMessage().getId(), READ.ordinal())) {
                msg.getMessage().setStatus(READ);
                return msg;
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

    public boolean canUpdateAvatar(Integer userId, Integer chatId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return false;
        var member = getMemberInfo(members, userId);
        return member != null && member.getRole().canChangeChatInfo();
    }

    public void saveAvatar(Integer chatId, String path) {
        chatMapper.saveAvatarById(chatId, path);
    }

    public void setUserRole(Integer userId, Integer chatId, PostChatRoleRequest request) {
        if (canChangeRole(userId, chatId, request.getMember(), request.getRole())) {
            chatMapper.saveChatRole(chatId, request.getMember(), request.getRole().getId());
        }
    }

    public void setUserStatus(Integer userId, Integer chatId, PostChatStatusRequest request) {
        if (canChangeStatus(userId,chatId, request.getMember())) {
            chatMapper.saveChatStatus(chatId, request.getMember(), request.getStatus().getId());
        }
    }
    private boolean canChangeStatus(Integer userId, Integer chatId, Integer memberId) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return false;
        var toChange = getMemberInfo(members, memberId);
        if (toChange == null) return false;
        var user = getMemberInfo(members, userId);
        return user != null && user.getRole().canChangeStatus(toChange.getRole());
    }

    private boolean canChangeRole(Integer userId, Integer chatId, Integer memberId, ChatRole to) {
        var members = chatMapper.findChatMembers(chatId);
        if (members == null) return false;
        ChatMemberInfo toChange = getMemberInfo(members, memberId);
        if (toChange == null) return false;
        var user = getMemberInfo(members, userId);
        return user != null && user.getRole().canChangeRole(toChange.getRole(), to);
    }

    public void joinChat(Integer userId, Integer chatId) {
        Chat chat = chatMapper.findChatById(chatId);
        if (chat == null) throw  new ChatException(ADDRESS_NOT_FOUND, chatId);
        var member = getMemberInfo(chat.getMembers(), userId);
        if (member == null || member.canJoinChat()) {
            chatMapper.saveChatStatus(chatId, userId,ChatStatus.STATUS_MEMBER.getId());
            return;
        }
        throw new ChatException(NOT_PERMS, chatId);

    }

    public void leaveChat(Integer userId, Integer chatId) {
        Chat chat = chatMapper.findChatById(chatId);
        if (chat == null) throw  new ChatException(ADDRESS_NOT_FOUND, chatId);
        var member = getMemberInfo(chat.getMembers(), userId);
        if (member == null || member.canLeaveChat()) {
            chatMapper.saveChatStatus(chatId, userId,ChatStatus.STATUS_LEAVE.getId());
            return;
        }
        throw new ChatException(NOT_PERMS, chatId);
    }

    private ChatMemberInfo getMemberInfo(List<ChatMemberInfo> list, Integer id) {
        for (var member : list) {
            if (member.isSameId(id)) {
                return member;
            }
        }
        return null;
    }

    private ChatMemberInfo getMemberInfo(List<ChatMemberInfo> list, String nickname) {
        for (var member : list) {
            if (member.isSameNickname(nickname)) {
                return member;
            }
        }
        return null;
    }
}
