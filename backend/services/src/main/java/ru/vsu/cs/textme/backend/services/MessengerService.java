package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.model.ChatMessage;
import ru.vsu.cs.textme.backend.db.model.DirectMessage;
import ru.vsu.cs.textme.backend.db.model.info.ChatMessageInfo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessengerService {
    private final DirectMapper directMapper;
    private final ChatMapper chatMapper;

    public MessengerService(DirectMapper directMapper, ChatMapper chatMapper) {
        this.directMapper = directMapper;
        this.chatMapper = chatMapper;
    }

    public List<DirectMessage> getDirects(Integer id, Integer page) {
        return directMapper.getAllDirects(id, page);
    }

    public List<DirectMessage> getDirectPage(Integer from, Integer to, Integer page) {
        return directMapper.getMessages(from, to, 128, page * 128);
    }

    public List<ChatMessageInfo> getChatList(Integer userId) {
        var chatList = chatMapper.getAllChats(userId);
        var list = new ArrayList<ChatMessageInfo>();
        if (chatList != null) {
            for (var chatInfo : chatList) {
                var chatMessages = getChatPage(chatInfo.getId(), 0);
                if (chatMessages == null || chatMessages.isEmpty()) continue;
                var chatMessage = chatMessages.get(0);
                list.add(new ChatMessageInfo(chatMessage.getUser(), chatInfo, chatMessage.getMessage()));
            }
        }
        return list;
    }

    public List<ChatMessageInfo> getChatPage(Integer userId, Integer chatId, Integer page) {
        var members = chatMapper.findChatMembers(chatId);
        if (members != null) {
            for (var member : members) {
                if (member.getMember().getId().equals(userId)) {
                    var list = chatMapper.getMessages(chatId, 128, page * 128);
                    return  (list == null) ? Collections.emptyList() : list.stream()
                            .map(msg -> new ChatMessageInfo(msg.getUser(), msg.getChat().getInfo(), msg.getMessage()))
                            .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }

    public List<ChatMessage> getChatPage(Integer chat, Integer page) {
        return chatMapper.getMessages(chat, 128, page * 128);
    }

}
