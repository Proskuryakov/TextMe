package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.*;

import java.util.List;


@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE email = #{email}")
    @Results(id = "userResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "roles", column= "id", javaType = List.class,  many = @Many(select = "findRolesByUserId")),
    })
    User findUserByEmail(String email);

    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    @ResultMap("userResult")
    User findUserByNickname(String nickname);

    @Select("SELECT * FROM create_user(#{nickname},#{email},#{password});")
    @ResultMap("userResult")
    User createUser(RegistrationRequest user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = "userInfoResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = "findAvatarByUserId")),
            @Result(property = "card", column= "card_id", one = @One(select = "findCardByUserId")),
    })
    UserProfileInfo findUserInfoById(int id);

    @Select("SELECT f.url FROM files as f WHERE f.id = #{id}")
    String findAvatarByUserId(int id);

    @Select("SELECT ar.* FROM user_app_role as uar, app_roles as ar WHERE uar.user_id = #{id} AND ar.id = uar.role_id")
    List<AppRole> findRolesByUserId(int id);

    @Select("SELECT * FROM cards WHERE cards.id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = Tag.class, many = @Many(select = "findTagsByCardId")),
    })
    Card findCardByUserId(int id);

    @Select("SELECT t.* FROM card_tag as ct, tags as t WHERE ct.card_id = #{id} AND t.id = ct.tag_id")
    List<AppRole> findTagsByCardId(int id);

    @Insert("CALL save_avatar(#{nickname}, #{fileName}, #{fileType})")
    void saveAvatarByNickname(String nickname, String fileName, int fileType);
}
