package ru.vsu.cs.textme.backend.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.vsu.cs.textme.backend.db.mapper.CardMapper;
import ru.vsu.cs.textme.backend.db.mapper.ChatMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.info.Card;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.User;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CardService {
    private final CardMapper cardMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ChatMapper chatMapper;

    public CardService(CardMapper cardMapper, UserService userService, UserMapper userMapper, ChatMapper chatMapper) {
        this.cardMapper = cardMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.chatMapper = chatMapper;
    }

    public Card findUserCardById(int userId) {
        return cardMapper.findCardByUserId(userId);
    }

    public Card findChatCardById(int chatId) {
        return cardMapper.findCardByChatId(chatId);
    }

    public void addUserTag(User user, String tag) {
        Card c = cardMapper.findCardByUserId(user.getId());
        if (c.getTags() != null && c.getTags().contains(tag.toLowerCase())) return;
        cardMapper.deleteCardTag(c.getId(), tag);
        addTag(cardMapper.findCardByUserId(user.getId()), tag);
    }

    public void addChatCard(Integer cardId, String tag) {
        addTag(cardMapper.findCardByChatId(cardId), tag);
    }

    public void deleteTag(User user, String tag) {
        Card c = cardMapper.findCardByUserId(user.getId());
        if (c.getTags() == null || !c.getTags().contains(tag.toLowerCase())) return;
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

    private static final Integer CARD_LIMIT = 10;

    public List<Profile> findNearbyUserProfilesById(Integer id, Integer page, @NotNull String tag) {
        List<String> list = StringUtils.hasText(tag) ?
                Collections.singletonList(tag) :
                cardMapper.findCardByUserId(id).getTags();
        return findUserProfilesByIdLike(id, page, list);
    }

    public List<Profile> findUserProfilesByIdLike(Integer id, Integer page, @Nullable List<String> tags) {
        if (tags == null) return Collections.emptyList();
        return userMapper.findSpecialProfiles(id, toString(tags), CARD_LIMIT, page * CARD_LIMIT);
    }

    public List<Profile> findNearbyChatProfilesById(Integer id, Integer page, @NotNull String tag) {
        List<String> list =  StringUtils.hasText(tag) ?
                Collections.singletonList(tag) :
                cardMapper.findCardByUserId(id).getTags();
        return findChatProfilesByIdLike(page, list);
    }

    public List<Profile> findChatProfilesByIdLike(Integer page, @Nullable List<String> tags) {
        if (tags == null) return Collections.emptyList();
        return chatMapper.findSpecialProfiles(toString(tags), CARD_LIMIT, page * CARD_LIMIT);
    }

    public List<Profile> findRandomUserProfiles() {
        return userMapper.findRandomProfiles(CARD_LIMIT);
    }

    public List<Profile> findRandomChatProfiles() {
        return chatMapper.findRandomProfiles(CARD_LIMIT);
    }

    private String toString(List<String> tags) {
        return '{' + String.join(",", tags) + '}';
    }
}
