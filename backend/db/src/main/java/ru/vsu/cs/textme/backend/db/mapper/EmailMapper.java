package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmailMapper {
    @Select("SELECT activate_email(#{uuid}, #{user_id})")
    String activateEmail(String uuid, Integer user_id);

    @Insert("INSERT INTO inactive_emails (email, uuid, user_id) VALUES(#{email}, #{uuid}, #{user_id}) ON CONFLICT DO NOTHING")
    int saveInactiveEmail(String email, String uuid, Integer user_id);
}
