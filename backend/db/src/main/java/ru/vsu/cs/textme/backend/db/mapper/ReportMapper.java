package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.Report;
import ru.vsu.cs.textme.backend.db.model.ReportData;
import ru.vsu.cs.textme.backend.db.model.TypedInfo;
import ru.vsu.cs.textme.backend.db.model.info.Info;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ReportMapper {
    String AVATAR_SELECT = "findAvatarById";
    String USER_INFO_SELECT = "findUserInfoById";

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = USER_INFO_SELECT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    Info findUserInfoById(Integer id);

    @Select("SELECT id, nickname as name, image_id, 'user' as info_type FROM users WHERE card_id = #{id}\n" +
            "UNION\n" +
            "SELECT id, title as name, image_id, 'chat' as info_type FROM chats WHERE card_id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "type", column = "info_type"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = AVATAR_SELECT)),
    })
    TypedInfo findCardOrUserInfoByCardId(Integer id);

    @Select("SELECT url FROM files WHERE id = #{id}")
    String findAvatarById(Integer id);

    @Insert("INSERT INTO reports (user_id, card_id, message) " +
            "VALUES (#{user},#{card},#{message}) ON CONFLICT (user_id, card_id) DO UPDATE \n" +
            "SET message = #{message}, reviewer_id = NULL, review_date = NULL")
    void setReport(Integer user, Integer card, String message);

    @Select("SELECT user_id, message FROM reports\n" +
            "WHERE card_id = {cardId} AND review_date IS NULL\n" +
            "LIMIT #{limit}\n" +
            "OFFSET #{offset}")
    @Results({
            @Result(property = "reporter", column = "user_id", one = @One(select = USER_INFO_SELECT)),
            @Result(property = "message", column = "message"),
            @Result(property = "date", column = "date_create")
    })
    List<Report> findReportProfilePage(Integer cardId, Integer limit, Integer offset);

    @Insert("CALL ban_card(#{by}, #{cardId}, #{expired}")
    void saveBan(Integer by, Integer cardId, Timestamp expired);

    @Update("UPDATE reports SET reviewer_id = #{moderId}, review_date = now() \n" +
            "WHERE user_id = #{reporterId} AND card_id = #{cardId};")
    void denyReport(Integer moderId, Integer reporterId, Integer cardId);
}
