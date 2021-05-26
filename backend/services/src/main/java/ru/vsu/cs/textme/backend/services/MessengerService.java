package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.DirectMapper;
import ru.vsu.cs.textme.backend.db.model.ChatMessage;
import ru.vsu.cs.textme.backend.db.model.DirectMessage;
import java.util.*;

@Service
public class MessengerService {
    private final DirectMapper directMapper;
    private final ChatMapper chatMapper;

    public MessengerService(DirectMapper directMapper, ChatMapper chatMapper) {
        this.directMapper = directMapper;
        this.chatMapper = chatMapper;
    }

    public Map<Integer, List<DirectMessage>> getDirects(Integer id) {
        List<Integer> directList = directMapper.getAllDirects(id);
        var map = new HashMap<Integer, List<DirectMessage>>();
        if (directList != null) {
            for (var nickname : directList) {
                map.put(id, getDirectPage(id, nickname, 0));
            }
        }
        return map;
    }

    public Map<Integer, List<ChatMessage>> getChats(Integer userId) {
        var chatList = chatMapper.getAllChats(userId);
        var map = new HashMap<Integer, List<ChatMessage>>();
        if (chatList != null) {
            for (var chat : chatList) {
                map.put(chat, getChatPage(chat, 0));
            }
        }
        return map;
    }

    public List<DirectMessage> getDirectPage(Integer from, Integer to, Integer page) {
        return directMapper.getMessages(from, to, 128, page * 128);
    }

    public List<ChatMessage> getChatPage(Integer chat, Integer page) {
        return chatMapper.getMessages(chat, 128, page * 128);
    }
    public List<ChatMessage> getChatPage(Integer userId, Integer chat, Integer page) {
        var members = chatMapper.findChatMembers(chat);
        if (members != null) {
            for (var m : members) {
                if (m.getUser().getId().equals(userId))
                    return chatMapper.getMessages(chat, 128, page * 128);
            }
        }
        return Collections.emptyList();
    }
}
