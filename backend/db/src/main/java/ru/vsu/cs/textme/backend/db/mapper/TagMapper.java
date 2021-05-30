package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import ru.vsu.cs.textme.backend.db.model.Tag;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("SELECT t.content FROM tags t\n" +
            "JOIN card_tag ct ON t.content LIKE #{begin} AND ct.tag_id = t.id\n" +
            "GROUP BY ct.tag_id, t.id\n" +
            "ORDER BY COUNT(*) DESC\n" +
            "LIMIT #{max};")
    List<String> getTagsLike(String begin, int max);
}
