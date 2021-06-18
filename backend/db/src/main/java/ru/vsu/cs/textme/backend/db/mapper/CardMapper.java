package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.info.Card;

import java.util.List;

@Mapper
public interface CardMapper {
    String FIND_TAGS = "findTagsByCardId";
    String CARD_RESULT = "cardResult";

    @Select("SELECT * FROM cards WHERE cards.id = #{cardId}")
    @Results(id = CARD_RESULT,value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = List.class, many = @Many(select = FIND_TAGS)),
    })
    Card findCardById(Integer cardId);

    @Select("SELECT * FROM cards c, chats ch WHERE ch.id = #{id} AND c.id = ch.card_id")
    @ResultMap(CARD_RESULT)
    Card findCardByChatId(int id);

    @Select("SELECT c.* FROM cards c, users u WHERE u.id = #{id} AND c.id = u.card_id")
    @ResultMap(CARD_RESULT)
    Card findCardByUserId(int id);

    @Select("SELECT t.content FROM card_tag as ct, tags as t WHERE ct.card_id = #{id} AND t.id = ct.tag_id")
    List<String> findTagsByCardId(int id);

    @Insert("INSERT INTO tags (content) SELECT lower(#{tag}) ON CONFLICT DO NOTHING;" +
            "INSERT INTO card_tag (card_id, tag_id) SELECT #{cardId}, id FROM tags WHERE content = lower(#{tag})")
    void saveCardTag(Integer cardId, String tag);

    @Update("UPDATE cards SET content = #{content} WHERE id = #{cardId}")
    void saveContentById(Integer cardId, String content);

    @Delete("DELETE FROM card_tag WHERE card_id = #{cardId} AND tag_id = " +
            "(SELECT t.id FROM tags t WHERE t.content = #{tag});")
    void deleteCardTag(Integer cardId, String tag);
}
