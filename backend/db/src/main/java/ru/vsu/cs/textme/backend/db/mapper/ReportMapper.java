package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.Report;
import ru.vsu.cs.textme.backend.db.model.ReportsData;
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
            "VALUES (#{user},#{card},#{message}) ON CONFLICT DO UPDATE")
    void addReport(Integer user, Integer card, String message);

    @Select("SELECT u.id, u.nickname as name, u.image_id,  r.card_id, 'user' as card_type,count(*) FROM reports r, users u\n" +
            "WHERE r.review_date IS NULL AND u.card_id = r.card_id\n" +
            "GROUP BY r.card_id, u.id, u.nickname, u.image_id\n" +
            "UNION\n" +
            "SELECT  c.id, c.title as name, c.image_id,r.card_id, 'chat' as card_type, count(*) FROM reports r, chats c\n" +
            "WHERE r.review_date IS NULL AND c.card_id = r.card_id\n" +
            "GROUP BY r.card_id, c.id, c.title, c.image_id\n" +
            "ORDER BY card_id DESC\n" +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results( value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "image", column = "image_id", one = @One(select = AVATAR_SELECT)),
            @Result(property = "card", column = "card_id"),
            @Result(property = "type", column = "card_type"),
            @Result(property = "count", column = "count")
    })
    List<ReportsData> findReportsDataPage(Integer limit, Integer offset);

    @Select("SELECT user_id, message FROM reports\n" +
            "WHERE card_id = 2 AND review_date IS NULL\n" +
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
}
