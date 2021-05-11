package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.CardMapper;
import ru.vsu.cs.textme.backend.db.model.Card;
import ru.vsu.cs.textme.backend.db.model.User;

@Service
public class CardService {
    private final CardMapper cardMapper;
    private final UserService userService;

    public CardService(CardMapper cardMapper, UserService userService) {
        this.cardMapper = cardMapper;
        this.userService = userService;
    }

    public Card findUserCardById(int userId) {
        return cardMapper.findCardByUserId(userId);
    }

    public Card findChatCardById(int chatId) {
        return cardMapper.findCardByChatId(chatId);
    }

    public void addUserTag(User user, String tag) {
        addTag(cardMapper.findCardByUserId(user.getId()), tag);
    }

    public void addChatCard(Integer cardId, String tag) {
        addTag(cardMapper.findCardByChatId(cardId), tag);
    }

    public void deleteTag(User user, String tag) {
        Card c = cardMapper.findCardByUserId(user.getId());
        if (c.getTags() != null && c.getTags().contains(tag.toLowerCase())) return;
        cardMapper.deleteCardTag(c.getId(), tag);
    }

    public static final int MAX_TAGS = 16;
    private void addTag(Card c, String tag) {
        if (c.getTags() != null && c.getTags().size() >= MAX_TAGS) return;
        cardMapper.saveCardTag(c.getId(), tag);
    }

    public void saveUserContent(User user, String content) {
        saveContentById(cardMapper.findCardByUserId(user.getId()), content);
    }

    public void saveChatContent(User user, Integer chatId, String content) {
        //todo
        //saveContentById(cardMapper.findCardByChatId(chatId), content);
    }

    private void saveContentById(Card c, String content) {
        cardMapper.saveContentById(c.getId(), content);
    }
}
