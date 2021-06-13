package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;

import java.util.List;

@Mapper
public interface DirectMapper {
    String USER_INFO_RESULT = "userInfoResult";
    String MESSAGE_RESULT = "messageResult";
    String DIRECT_MESSAGE_RESULT = "directMsgResult";
    String AVATAR_SELECT = "findAvatarById";
    String MESSAGE_STATUS_SELECT = "findMessageStatusById";

    @Select("SELECT content FROM message_statuses WHERE id = #{id}")
    MessageStatus findMessageStatusById(Integer id);

    @Select("SELECT url FROM files WHERE id = #{id}")
    String findAvatarById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = USER_INFO_RESULT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    Info findInfoById(Integer id);

    @Select("SELECT * FROM messages WHERE id = #{id}")
    @Results(id = MESSAGE_RESULT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "dateCreate", column = "date_create"),
            @Result(property = "dateUpdate", column = "date_update"),
            @Result(property = "status", column= "status_id", javaType = MessageStatus.class, one = @One(select = MESSAGE_STATUS_SELECT)),
    })
    Message findMessageById(Integer id);

    @Select("SELECT * FROM direct_messages WHERE message_id = #{id}")
    @Results(id = DIRECT_MESSAGE_RESULT, value = {
            @Result(property = "from", column = "user_from_id", javaType = Info.class, one = @One(resultMap = USER_INFO_RESULT)),
            @Result(property = "to", column = "user_to_id", javaType = Info.class, one = @One(resultMap = USER_INFO_RESULT)),
            @Result(property = "message", column = "message_id",javaType = Message.class, one = @One(resultMap = MESSAGE_RESULT)),
    })
    MessageInfo findDirectMessageById(Integer id);

    @Select("SELECT * FROM new_direct_msg(#{from}, #{to}, #{message})")
    @ResultMap(DIRECT_MESSAGE_RESULT)
    MessageInfo save(String from, String to, String message);

    @Update("UPDATE messages SET content = #{msg}, date_update = now() WHERE id = #{id};")
    @ResultMap(DIRECT_MESSAGE_RESULT)
    void update(String msg, Integer id);

    @Update("UPDATE messages SET status_id = #{status} WHERE id = #{id};")
    @ResultMap(DIRECT_MESSAGE_RESULT)
    boolean setStatusById(Integer id, Integer status);

    @Select("SELECT dm.* FROM direct_messages dm, messages m \n" +
            "WHERE (dm.user_from_id = #{from} AND dm.user_to_id = #{to} " +
            "   OR dm.user_from_id = #{to} AND dm.user_to_id = #{from}) \n" +
            "   AND dm.message_id = m.id AND m.status_id != 2\n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap(DIRECT_MESSAGE_RESULT)
    List<MessageInfo> getMessages(Integer from, Integer to, Integer limit, Integer offset);

    @Select("SELECT dm.* FROM direct_messages dm, messages m\n" +
            "WHERE user_from_id = #{id} AND m.id = dm.message_id AND m.status_id != 2\n" +
            "UNION\n" +
            "SELECT dm.* FROM direct_messages dm, messages m\n" +
            "WHERE user_to_id = #{id}  AND m.id = dm.message_id AND m.status_id != 2;")
    @ResultMap(DIRECT_MESSAGE_RESULT)
    List<MessageInfo> getAllDirects(Integer id, Integer page);
}
