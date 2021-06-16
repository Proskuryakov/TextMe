package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.MessageInfo;

import java.util.List;

@Mapper
public interface DirectMapper {
    String USER_INFO_SELECT = "findInfoById";
    String MESSAGE_SELECT = "findMessageById";
    String MESSAGE_INFO_SELECT = "findDirectMessageById";
    String AVATAR_SELECT = "findAvatarById";
    String MESSAGE_STATUS_SELECT = "findMessageStatusById";
    String MESSAGE_FILES_SELECT = "findMessageFilesById";

    @Select("SELECT content FROM message_statuses WHERE id = #{id}")
    MessageStatus findMessageStatusById(Integer id);

    @Select("SELECT f.url FROM message_file mf, files f WHERE mf.message_id = #{id} AND f.id = mf.file_id")
    List<String> findMessageFilesById(Integer id);

    @Select("SELECT url FROM files WHERE id = #{id}")
    String findAvatarById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = USER_INFO_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    Info findInfoById(Integer id);

    @Select("SELECT * FROM messages WHERE id = #{id}")
    @Results(id = MESSAGE_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "dateCreate", column = "date_create"),
            @Result(property = "dateUpdate", column = "date_update"),
            @Result(property = "status", column= "status_id", javaType = MessageStatus.class, one = @One(select = MESSAGE_STATUS_SELECT)),
    })
    Message findMessageById(Integer id);

    @Select("SELECT * FROM direct_messages WHERE message_id = #{id}")
    @Results(id = MESSAGE_INFO_SELECT, value = {
            @Result(property = "from", column = "user_from_id", javaType = Info.class, one = @One(select = USER_INFO_SELECT)),
            @Result(property = "to", column = "user_to_id", javaType = Info.class, one = @One(select = USER_INFO_SELECT)),
            @Result(property = "message", column = "message_id",javaType = Message.class, one = @One(select = MESSAGE_SELECT)),
            @Result(property = "images", column = "message_id",javaType = List.class, many = @Many(select = MESSAGE_FILES_SELECT)),
    })
    MessageInfo findDirectMessageById(Integer id);

    @Select("SELECT * FROM new_direct_msg(#{from}, #{to}, #{message});")
    @Results({
            @Result(property = "from", column = "user_from_id", javaType = Info.class, one = @One(select = USER_INFO_SELECT)),
            @Result(property = "to", column = "user_to_id", javaType = Info.class, one = @One(select = USER_INFO_SELECT)),
            @Result(property = "message", column = "message_id",javaType = Message.class, one = @One(select = MESSAGE_SELECT)),
            @Result(property = "images", column = "message_id",javaType = List.class, many = @Many(select = MESSAGE_FILES_SELECT)),
    })
    MessageInfo save(Integer from, Integer to, String message);

    @Update("UPDATE messages SET content = #{msg}, date_update = now() WHERE id = #{id};")
    @ResultMap(MESSAGE_INFO_SELECT)
    void update(String msg, Integer id);

    @Update("UPDATE messages SET status_id = #{status} WHERE id = #{id};")
    @ResultMap(MESSAGE_INFO_SELECT)
    boolean setStatusById(Integer id, Integer status);

    @Select("SELECT dm.* FROM direct_messages dm, messages m \n" +
            "WHERE (dm.user_from_id = #{from} AND dm.user_to_id = #{to} " +
            "   OR dm.user_from_id = #{to} AND dm.user_to_id = #{from}) \n" +
            "   AND dm.message_id = m.id AND m.status_id != 2\n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap(MESSAGE_INFO_SELECT)
    List<MessageInfo> getMessages(Integer from, Integer to, Integer limit, Integer offset);

    @Select("WITH lm AS (SELECT dm.*, MAX(m.date_create) as date_create FROM direct_messages dm, messages m\n" +
            "    WHERE (dm.user_from_id = #{id} OR dm.user_to_id = #{id}) AND m.id = dm.message_id  AND m.status_id != 2\n" +
            "    GROUP BY dm.user_from_id, dm.user_to_id, dm.message_id)\n" +
            "SELECT lm2.user_from_id, lm2.user_to_id, lm2.message_id FROM lm AS lm1\n" +
            "JOIN lm AS lm2 ON lm2.user_to_id = lm2.user_from_id AND\n" +
            "                  lm2.date_create > lm2.date_create OR\n" +
            "                  lm1.user_to_id = lm2.user_from_id AND\n" +
            "                  lm2.user_to_id = lm1.user_from_id AND\n" +
            "                  lm1.date_create < lm2.date_create\n" +
            "GROUP BY lm2.user_from_id, lm2.user_to_id, lm2.message_id\n" +
            "LIMIT #{limit}\n" +
            "OFFSET #{offset};")
    @ResultMap(MESSAGE_INFO_SELECT)
    List<MessageInfo> getLastMessageAllDirects(Integer id, Integer limit, Integer offset);

    @Insert("CALL save_message_file(#{id}, #{path})")
    void saveMessageFile(Integer id, String path);
}
