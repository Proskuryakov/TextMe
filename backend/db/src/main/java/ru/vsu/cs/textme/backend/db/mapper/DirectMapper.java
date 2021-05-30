package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Info;

import java.util.List;

@Mapper
public interface DirectMapper {
    @Select("SELECT nickname FROM users WHERE id = #{id}")
    String findNicknameById(Integer id);

    @Select("SELECT * FROM direct_messages WHERE message_id = #{id}")
    @Results(id = "directMsgResult", value = {
            @Result(property = "from", column = "user_from_id", one = @One(select = "findNicknameById")),
            @Result(property = "to", column = "user_to_id", one = @One(select = "findNicknameById")),
            @Result(property = "message", column= "message_id", one = @One(select = "findMessageById")),
    })
    DirectMessage findDirectMessageById(Integer id);

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


    @Select("SELECT FROM new_direct_msg(#{from}, #{to}, #{message})")
    @ResultMap("directMsgResult")
    DirectMessage save(String from, String to, String message);

    @Update("UPDATE messages SET content = #{msg}, date_update = now() WHERE id = #{id};" +
            "SELECT * FROM direct_messages WHERE message_id = #{id};")
    @ResultMap("directMsgResult")
    DirectMessage update(String msg, Integer id);

    @Update("UPDATE messages SET status_id = #{status} WHERE id = #{id};" +
            "SELECT * FROM direct_messages WHERE message_id = #{id};")
    @ResultMap("directMsgResult")
    DirectMessage setStatusById(Integer id, Integer status);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = "userInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = "findAvatarByUserId")),
    })
    Info findUserInfoById(Integer userId);

    @Select("SELECT dm.* FROM direct_messages dm, messages m \n" +
            "WHERE (dm.user_from_id = #{from} AND dm.user_to_id = #{to} " +
            "   OR dm.user_from_id = #{to} AND dm.user_to_id = #{from}) \n" +
            "   AND dm.message_id = m.id AND m.status_id != 2\n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap("directMsgResult")
    List<DirectMessage> getMessages(Integer from, Integer to, Integer limit, Integer offset);

    @Select("SELECT dm.user_to_id as id FROM direct_messages dm, messages m\n" +
            "WHERE user_from_id = #{id} AND m.id = dm.message_id AND m.status_id != 2\n" +
            "UNION\n" +
            "SELECT dm.user_from_id as id FROM direct_messages dm, messages m\n" +
            "WHERE user_to_id = #{id}  AND m.id = dm.message_id AND m.status_id != 2;")
    @ResultMap("userInfoResult")
    List<Info> getAllDirects(Integer id);
}
