package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.*;

import java.util.List;

@Mapper
public interface ChatMapper {
    String CHAT_SELECT = "findChatById";
    String CHAT_INFO_SELECT = "findChatInfoById";
    String CARD_SELECT = "findCardById";
    String CHAT_PROFILE_SELECT = "findProfileById";
    String CHAT_MESSAGE_SELECT = "findChatMessageByMessageId";
    String MESSAGE_SELECT = "findMessageById";
    String MESSAGE_STATUS_SELECT = "findMessageStatusById";
    String MESSAGE_INFO_SELECT = "findChatMessageInfoById";
    String MESSAGE_FILES_SELECT = "findMessageFilesById";
    String USER_INFO_SELECT = "findUserInfoById";
    String CHAT_MEMBERS_SELECT = "findChatMembers";

    String AVATAR_SELECT = "findAvatarById";
    String TAGS_SELECT = "findTagsByCardId";

    @Select("SELECT url FROM files WHERE id = #{id}")
    String findAvatarById(Integer id);

    @Select("SELECT f.url FROM message_file mf, files f WHERE mf.message_id = #{id} AND f.id = mf.file_id")
    List<String> findMessageFilesById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = USER_INFO_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    Info findUserInfoById(Integer userId);

    @Select("SELECT * FROM chats WHERE id = #{id}")
    @Results(id = CHAT_INFO_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "title"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    Info findChatInfoById(Integer id);

    @Select("SELECT * FROM chats WHERE id = #{id}")
    @Results(id = CHAT_PROFILE_SELECT, value = {
            @Result(property = "info", column = "id", one = @One(select = CHAT_INFO_SELECT)),
            @Result(property = "card", column = "card_id", one = @One(select = CARD_SELECT)),
    })
    Profile findProfileById(Integer id);

    @Select("SELECT * FROM cards WHERE cards.id = #{cardId}")
    @Results(id = CARD_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = Tag.class, many = @Many(select = TAGS_SELECT)),
    })
    Card findCardById(Integer cardId);

    @Select("SELECT t.content FROM card_tag as ct, tags as t WHERE ct.card_id = #{cardId} AND t.id = ct.tag_id")
    List<String> findTagsByCardId(Integer cardId);

    @Select("SELECT content FROM message_statuses WHERE id = #{id}")
    @Results(id = MESSAGE_STATUS_SELECT)
    MessageStatus findMessageStatusById(Integer id);

    @Select("SELECT * FROM chats WHERE id = #{id}")
    @Results(id = CHAT_SELECT, value = {
            @Result(property = "info", column = "id", one = @One(resultMap = CHAT_INFO_SELECT)),
            @Result(property = "members", column = "id", javaType = List.class, many = @Many(select = CHAT_MEMBERS_SELECT)),
    })
    Chat findChatById(Integer id);

    @Select("SELECT user_id, role_id FROM user_chat_role WHERE chat_id = #{id}")
    @Results(id = CHAT_MEMBERS_SELECT, value = {
            @Result(property = "member", column = "user_id", one = @One(select = USER_INFO_SELECT)),
            @Result(property = "role", column = "role_id", javaType =  ChatRole.class,
                    typeHandler = org.apache.ibatis.type.EnumOrdinalTypeHandler.class),
    })
    List<ChatMemberInfo> findChatMembers(Integer id);

    @Select("SELECT * FROM chat_messages WHERE message_id = #{id}")
    @Results(id = MESSAGE_INFO_SELECT, value = {
            @Result(property = "from", column = "user_id", javaType = Info.class, one = @One(select = USER_INFO_SELECT)),
            @Result(property = "to", column = "chat_id", javaType = Info.class, one = @One(select = CHAT_INFO_SELECT)),
            @Result(property = "message", column = "message_id",javaType = Message.class, one = @One(select = MESSAGE_SELECT)),
            @Result(property = "images", column = "message_id",javaType = List.class, many = @Many(select = MESSAGE_FILES_SELECT)),
    })
    MessageInfo findChatMessageInfoById(Integer id);

    @Select("SELECT * FROM chat_messages WHERE message_id = #{id}")
    @Results(id = CHAT_MESSAGE_SELECT, value = {
            @Result(property = "info", column = "message_id", one = @One(select = MESSAGE_INFO_SELECT)),
            @Result(property = "members", column = "chat_id", many = @Many(select = CHAT_MEMBERS_SELECT)),
    })
    ChatMessage findChatMessageByMessageId(Integer id);

    @Select("SELECT * FROM messages WHERE id = #{id}")
    @Results(id = MESSAGE_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "dateCreate", column = "date_create"),
            @Result(property = "dateUpdate", column = "date_update"),
            @Result(property = "status", column= "status_id", one = @One(select = MESSAGE_STATUS_SELECT)),
    })
    Message findMessageById(Integer id);

    @Update("UPDATE messages SET status_id = #{status} WHERE id = #{id};")
    @ResultMap(CHAT_MESSAGE_SELECT)
    boolean setStatusById(Integer id, Integer status);

    @Select("SELECT FROM new_chat_msg(#{from}, #{to}, #{message})")
    @ResultMap(CHAT_MESSAGE_SELECT)
    ChatMessage save(Integer from, Integer to, String message);

    @Update("UPDATE messages SET content = #{msg}, date_update = now() WHERE id = #{id};" +
            "SELECT * FROM chat_messages WHERE message_id = #{id};")
    @ResultMap(CHAT_MESSAGE_SELECT)
    ChatMessage update(String msg, Integer id);

    @Select("SELECT cm.*, MAX(m.date_create) as date_create FROM chats c, user_chat_role uc, chat_messages cm, messages m\n" +
            "WHERE uc.user_id = #{userId} AND\n" +
            "      uc.chat_id = c.id AND\n" +
            "      uc.role_id != 4 AND\n" +
            "      uc.role_id != 5 AND\n" +
            "      cm.chat_id = c.id AND\n" +
            "      cm.message_id = m.id AND\n" +
            "      m.status_id != 2\n" +
            "GROUP BY cm.user_id, cm.chat_id, cm.message_id\n" +
            "LIMIT #{limit}\n" +
            "OFFSET #{offset}"
    )
    @ResultMap(MESSAGE_INFO_SELECT)
    List<MessageInfo> getLastMessageAllChats(Integer userId, Integer limit, Integer offset);

    @Select("SELECT cm.* FROM chat_messages cm, messages m \n" +
            "WHERE cm.chat_id = #{chat} AND cm.message_id = m.id AND m.status_id != 2\n" +
            "LIMIT #{limit} OFFSET #{offset}\n")
    @ResultMap(MESSAGE_INFO_SELECT)
    List<MessageInfo> getMessages(Integer chat, Integer limit, Integer offset);

    @Select("SELECT * FROM chats ORDER BY random() LIMIT #{limit}")
    @ResultMap(CHAT_PROFILE_SELECT)
    List<Profile> findRandomProfiles(Integer limit);

    @Select("SELECT ch.id AS id FROM chats ch\n" +
            "    JOIN card_tag ct ON ct.card_id = ch.card_id\n" +
            "    JOIN tags t ON t.id = ct.tag_id AND t.content = ANY(#{tags}::varchar[])\n" +
            "GROUP BY ch.id\n" +
            "ORDER BY count(*) DESC\n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap(CHAT_PROFILE_SELECT)
    List<Profile> findSpecialProfiles(String tags, Integer limit, Integer offset);

    @Select("SELECT * FROM create_chat(#{userId}, #{name})")
    @ResultMap(CHAT_SELECT)
    Chat createChat(Integer userId, String name);

    @Delete("DELETE FROM chats WHERE id = #{id}")
    boolean deleteChat(Integer chatId);

    @Update("UPDATE chats SET title = #{title} WHERE id = #{id}")
    boolean setTitle(Integer id, String title);

    @Update("CALL save_chat_avatar(#{chatId}, #{path})")
    boolean saveAvatarById(Integer chatId, String path);

    @Insert("INSERT INTO user_chat_role (user_id, chat_id, role_id) VALUES(#{userId},#{chatId},#{roleId}) ON CONFLICT DO UPDATE")
    void saveChatRole(Integer chatId, Integer userId, Integer roleId);

    @Insert("INSERT INTO user_chat_status (user_id, chat_id, status_id) VALUES(#{userId},#{chatId},#{statusId}) ON CONFLICT DO UPDATE")
    void saveChatStatus(Integer chatId, Integer userId, Integer statusId);
}
