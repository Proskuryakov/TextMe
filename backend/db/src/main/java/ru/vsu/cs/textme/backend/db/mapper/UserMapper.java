package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;

import java.util.List;


@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = "userResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "roles", column= "id", javaType = List.class,  many = @Many(select = "findRolesByUserId")),
    })
    User findUserById(Integer userId);

    @Select("SELECT * FROM users WHERE email = #{email}")
    @ResultMap("userResult")
    User findUserByEmail(String email);

    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    @ResultMap("userResult")
    User findUserByNickname(String nickname);

    @Select("SELECT * FROM create_user(#{nickname},#{email},#{password});")
    @ResultMap("userResult")
    User createUser(RegistrationRequest user);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = "userInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = "findAvatarByUserId")),
            @Result(property = "card", column= "card_id", one = @One(select = "findCardById")),
    })
    UserProfileInfo findUserInfoById(Integer userId);

    @Select("SELECT f.url FROM files as f WHERE f.id = #{userId}")
    String findAvatarByUserId(Integer userId);

    @Select("SELECT ar.* FROM user_app_role as uar, app_roles as ar WHERE uar.user_id = #{userId} AND ar.id = uar.role_id")
    List<AppRole> findRolesByUserId(Integer userId);

    @Select("SELECT * FROM cards WHERE cards.id = #{cardId}")
    @Results(id = "cardResult",value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = Tag.class, many = @Many(select = "findTagsByCardId")),
    })
    Card findCardById(Integer cardId);

    @Select("SELECT t.content FROM card_tag as ct, tags as t WHERE ct.card_id = #{cardId} AND t.id = ct.tag_id")
    List<String> findTagsByCardId(Integer cardId);

    @Update("CALL save_avatar(#{nickname}, #{fileName}, #{fileType})")
    void saveAvatarByNickname(String nickname, String fileName, Integer fileType);

    @Select("INSERT INTO user_app_role (user_id, role_id) VALUES (#{userId}, #{roleId}) ON CONFLICT DO NOTHING;" +
            "SELECT * FROM users WHERE id = #{userId};")
    @ResultMap("userResult")
    User saveRoleByUserId(Integer userId, Integer roleId);

    @Select("SELECT * FROM activate_email(#{code})")
    @ResultMap("userResult")
    User activateEmail(String code);

    @Insert("INSERT INTO inactive_emails (email, uuid, user_id) VALUES(#{email}, #{code}, #{user_id}) ON CONFLICT DO NOTHING")
    int saveInactiveEmail(String email, String code, Integer user_id);
}
