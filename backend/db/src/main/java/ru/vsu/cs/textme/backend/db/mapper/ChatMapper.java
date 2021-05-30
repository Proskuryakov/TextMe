package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Card;
import ru.vsu.cs.textme.backend.db.model.info.CardInfo;
import ru.vsu.cs.textme.backend.db.model.info.ChatMemberInfo;

import java.util.List;

@Mapper
public interface ChatMapper {
    @Select("SELECT nickname FROM users WHERE id = #{id}")
    String findNicknameById(Integer id);

    @Select("SELECT * FROM chats WHERE id = #{id}")
    @Results(id = "chatInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "members", column = "id", javaType = List.class, many = @Many(select = "findChatMembers")),
    })
    Chat findChatInfoById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = "userInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = "findAvatarByUserId")),
            @Result(property = "card", column = "card_id", one = @One(select = "findCardById")),
    })
    CardInfo findUserInfoById(Integer userId);

    @Select("SELECT user_id, role_id FROM user_chat_role WHERE chat_id = #{id}")
    @Results(id = "memberResult", value = {
            @Result(property = "user", column = "user_id", one = @One(select = "findUserInfoById")),
            @Result(property = "role", column = "role_id", one = @One(select = "findChatRoleById")),
    })
    List<ChatMemberInfo> findChatMembers(Integer id);

    @Select("SELECT content FROM chat_roles WHERE id = #{id}")
    ChatRole findChatRoleById(Integer id);

    @Select("SELECT * FROM chat_messages WHERE message_id = #{id}")
    @Results(id = "chatMsgResult", value = {
            @Result(property = "chat", column = "chat_id", one = @One(select = "findChatById")),
            @Result(property = "user", column = "user_id", one = @One(select = "findNicknameById")),
            @Result(property = "message", column = "message_id", one = @One(select = "findMessageById")),
    })
    ChatMessage findChatMessageById(Integer id);

    @Select("SELECT content FROM message_statuses WHERE id = #{id}")
    MessageStatus findMessageStatusById(Integer id);

    @Select("SELECT * FROM messages WHERE id = #{id}")
    @Results(id = "messageResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "dateCreate", column = "date_create"),
            @Result(property = "dateUpdate", column = "date_update"),
            @Result(property = "status", column= "status_id", one = @One(select = "findMessageStatusById")),
    })
    Message findMessageById(Integer id);

    @Select("UPDATE messages SET status_id = #{status} WHERE id = #{id};" +
            "SELECT * FROM chat_messages WHERE message_id = #{id};")
    @ResultMap("chatMsgResult")
    ChatMessage setStatusById(Integer id, Integer status);

    @Select("SELECT FROM new_chat_msg(#{from}, #{to}, #{message})")
    @ResultMap("chatMsgResult")
    ChatMessage save(String from, Integer to, String message);

    @Update("UPDATE messages SET content = #{msg}, date_update = now() WHERE id = #{id};" +
            "SELECT * FROM chat_messages WHERE message_id = #{id};")
    @ResultMap("chatMsgResult")
    ChatMessage update(String msg, Integer id);


    @Select("SELECT * FROM cards WHERE cards.id = #{cardId}")
    @Results(id = "cardResult",value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = Tag.class, many = @Many(select = "findTagsByCardId")),
    })
    Card findCardById(Integer cardId);

    @Select("SELECT * FROM chats WHERE id = #{id}")
    @Results(id = "chatCardInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "title"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = "findAvatarByChatId")),
            @Result(property = "card", column = "card_id", one = @One(select = "findCardById")),
    })
    CardInfo findChatProfileInfoById(Integer id);

    @Select("WITH user_tags AS (SELECT tag_id " +
            "   FROM card_tag ct, cards c, users u " +
            "   WHERE u.id = #{id} AND u.card_id = c.id AND c.id = ct.card_id) " +
            "SELECT ch.id AS id FROM cards c " +
            "    JOIN chats ch ON ch.card_id = c.id" +
            "    JOIN card_tag ct ON c.id = ct.card_id " +
            "    JOIN user_tags ut ON ut.tag_id = ct.tag_id " +
            "GROUP BY ch.id " +
            "ORDER BY count(*) DESC " +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap("chatCardInfoResult")
    List<CardInfo> findNearbyChatCards(Integer id, Integer limit, Integer offset);

    @Select("SELECT c.id FROM chats c, user_chat_role uc\n" +
            "WHERE uc.user_id = #{userId} AND uc.chat_id = c.id AND uc.role_id != 3")
    List<Integer> getAllChats(Integer userId);

    @Select("SELECT cm.* FROM chat_messages cm, messages m \n" +
            "WHERE chat_id = #{chat} AND cm.message_id = m.id AND m.status_id != 2\n" +
            "LIMIT #{limit} OFFSET #{offset}\n")
    @ResultMap("chatMsgResult")
    List<ChatMessage> getMessages(Integer chat, Integer limit, Integer offset);
}
