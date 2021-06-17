package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import ru.vsu.cs.textme.backend.db.model.Tag;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("SELECT t.content FROM tags t\n" +
            "LEFT JOIN card_tag ct ON  ct.tag_id = t.id\n" +
            "WHERE t.content LIKE #{begin}\n" +
            "GROUP BY ct.tag_id, t.id\n" +
            "ORDER BY COUNT(ct.tag_id) DESC\n" +
            "LIMIT #{limit};")
    List<String> getTagsLike(String begin, int limit);
}
