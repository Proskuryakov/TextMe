package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.model.ChatMessage;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;

import java.util.*;
import java.util.stream.Collectors;

import static ru.vsu.cs.textme.backend.db.model.info.MessageInfo.DestinationType.CHAT;
import static ru.vsu.cs.textme.backend.db.model.info.MessageInfo.DestinationType.DIRECT;

@Service
public class MessengerService {
    private final DirectMapper directMapper;
    private final ChatMapper chatMapper;

    public MessengerService(DirectMapper directMapper, ChatMapper chatMapper) {
        this.directMapper = directMapper;
        this.chatMapper = chatMapper;
    }

    public List<MessageInfo> getDirects(Integer id, Integer page) {
        var messages =  directMapper.getLastMessageAllDirects(id, 128, page * 128);
        if (messages == null) return Collections.emptyList();
        for (var info : messages) {
            info.setDestination(DIRECT);
        }
        return messages;
    }

    public List<MessageInfo> getDirectPage(Integer from, Integer to, Integer page) {
        return directMapper.getMessages(from, to, 128, page * 128);
    }

    public List<MessageInfo> getChats(Integer userId, Integer page) {
        var messages = chatMapper.getLastMessageAllChats(userId, 128, page * 128);
        if (messages == null)  return Collections.emptyList();

        for (var chatMessage : messages) {
            chatMessage.setDestination(CHAT);
        }
        return messages;
    }

    public List<MessageInfo> getChatPage(Integer userId, Integer chatId, Integer page) {
        var members = chatMapper.findChatMembers(chatId);
        if (members != null) {
            for (var member : members) {
                if (member.getMember().getId().equals(userId)) {
                    if (member.canRead()) {
                        var list = getChatPage(chatId, page);
                        return (list == null) ? Collections.emptyList() : list.stream()
                                .peek(chatMessage -> chatMessage.setDestination(CHAT))
                                .collect(Collectors.toList());
                    }
                    break;
                }
            }
        }
        return Collections.emptyList();
    }

    private List<MessageInfo> getChatPage(Integer chat, Integer page) {
        return chatMapper.getMessages(chat, 128, page * 128);
    }
}
