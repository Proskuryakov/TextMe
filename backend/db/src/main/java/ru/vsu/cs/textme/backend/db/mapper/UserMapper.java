package ru.vsu.cs.textme.backend.db.mapper;

import org.apache.ibatis.annotations.*;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.Blocked;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.info.Card;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.RegistrationRequest;

import java.util.List;


@Mapper
public interface UserMapper {
    String USER_RESULT = "userResult";
    String FIND_CARD = "findCardById";
    String FIND_USER_INFO = "findInfoById";
    String FIND_PROFILE = "findProfileById";
    String FIND_AVATAR = "findAvatarById";
    String FIND_USER_ROLES = "findRolesByUserId";
    String FIND_TAGS = "findTagsByCardId";
    String FIND_CURRENT_BLOCKED = "findLastBlockUserById";

    @Select("SELECT * FROM banned_users WHERE user_banned_id = #{id} AND time_to > now() LIMIT(1)")
    @Results(value = {
            @Result(property = "from", column = "time_from"),
            @Result(property = "to", column = "time_to")
    })
    Blocked findLastBlockUserById(int id);

    @Select("SELECT t.content FROM card_tag as ct, tags as t WHERE ct.card_id = #{id} AND t.id = ct.tag_id")
    List<String> findTagsByCardId(int id);

    @Select("SELECT * FROM cards WHERE cards.id = #{cardId}")
    @Results(id = FIND_CARD, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "tags", column = "id", javaType = List.class, many = @Many(select = FIND_TAGS)),
    })
    Card findCardById(Integer cardId);

    @Select("SELECT url FROM files WHERE id = #{id}")
    String findAvatarById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(id = FIND_USER_INFO, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "nickname"),
            @Result(property = "imageUrl", column = "image_id", one = @One(select = FIND_AVATAR)),
    })
    Info findInfoById(Integer id);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = FIND_PROFILE, value = {
            @Result(property = "card", column = "card_id", javaType = Card.class, one = @One(select = FIND_CARD)),
            @Result(property = "info", column = "id", javaType = Info.class, one = @One(select = FIND_USER_INFO)),
    })
    Profile findProfileById(Integer userId);

    @Select("SELECT * FROM users WHERE nickname = #{name}")
    @ResultMap(FIND_PROFILE)
    Profile findProfileByName(String name);

    @Select("SELECT * FROM users WHERE id = #{userId}")
    @Results(id = USER_RESULT, value = {
            @Result(property = "id", column = "id"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "password", column = "password"),
            @Result(property = "email", column = "email"),
            @Result(property = "roles", column = "id", javaType = List.class, many = @Many(select = FIND_USER_ROLES)),
            @Result(property = "blocked", column = "id", javaType = Blocked.class, one = @One(select = FIND_CURRENT_BLOCKED)),
    })
    User findUserById(Integer userId);



    @Select("SELECT * FROM users WHERE email = #{email}")
    @ResultMap(USER_RESULT)
    User findUserByEmail(String email);

    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    @ResultMap(USER_RESULT)
    User findUserByNickname(String nickname);

    @Select("SELECT * FROM create_user(#{nickname},#{email},#{password});")
    @ResultMap(USER_RESULT)
    User createUser(RegistrationRequest request);

    @Select("SELECT f.url FROM files f, users u WHERE u.id = 4 AND f.id = u.image_id")
    String findAvatarByUserId(Integer userId);

    @Select("SELECT ar.* FROM user_app_role as uar, app_roles as ar WHERE uar.user_id = #{userId} AND ar.id = uar.role_id")
    List<AppRole> findRolesByUserId(Integer userId);


    @Select("INSERT INTO user_app_role (user_id, role_id) VALUES (#{userId}, #{roleId}) ON CONFLICT DO NOTHING;" +
            "SELECT * FROM users WHERE id = #{userId};")
    @ResultMap(USER_RESULT)
    User saveRoleByUserId(Integer userId, Integer roleId);

    @Update("UPDATE users SET nickname = #{nickname} WHERE id = #{userId} AND NOT EXISTS(SELECT FROM users WHERE nickname = #{nickname})")
    boolean saveNickname(Integer userId, String nickname);

    @Select("SELECT bu.user_who_id FROM blocked_users bu WHERE " +
            "bu.user_blocked_id = #{one} AND " +
            "bu.user_who_id = #{two} OR " +
            "bu.user_blocked_id = #{two} AND " +
            "bu.user_who_id = #{one}")
    Integer findBlocked(Integer one, Integer two);

    @Select("SELECT u.id, u.card_id FROM users u WHERE id != #{forUser} ORDER BY random() LIMIT #{limit}")
    @ResultMap(FIND_PROFILE)
    List<Profile> findRandomProfiles(Integer forUser, Integer limit);

    @Select("SELECT u.id, u.card_id FROM users u\n" +
            "    JOIN card_tag ct ON u.card_id = ct.card_id AND u.id != #{id}\n" +
            "    JOIN tags t ON t.id = ct.tag_id AND t.content =ANY(#{tags}::varchar[])\n" +
            "GROUP BY u.id, u.card_id\n" +
            "ORDER BY count(*) DESC\n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap(FIND_PROFILE)
    List<Profile> findSpecialProfiles(Integer id, String tags, Integer limit, Integer offset);

    @Update("UPDATE users SET password = #{pass} WHERE users.id = #{id} RETURNING *")
    void savePassword(Integer id, String pass);

    @Select("SELECT * FROM activate_email(#{code})")
    @ResultMap(USER_RESULT)
    User activateEmail(String code);

    @Insert("INSERT INTO inactive_emails (email, uuid, user_id) VALUES(#{email}, #{code}, #{user_id}) ON CONFLICT DO NOTHING")
    int saveInactiveEmail(String email, String code, Integer user_id);

    @Select("SELECT uuid FROM inactive_emails WHERE user_id = #{id} AND email = #{email}")
    String getInactiveUuid(Integer id, String email);

    @Update("CALL save_avatar(#{userId}, #{path})")
    boolean saveAvatarById(int userId, String path);

    @Insert("INSERT INTO blocked_users (user_who_id, user_blocked_id) VALUES(#{object}, #{subject}) ON CONFLICT DO NOTHING")
    void saveBlocked(int object, int subject);

    @Delete("DELETE FROM blocked_users WHERE user_who_id = #{object} AND user_blocked_id = #{subject}")
    boolean removeBlocked(int object, int subject);

    @Insert("INSERT INTO user_app_role(user_id, role_id) VALUES (#{user}, #{role}) ON CONFLICT DO NOTHING ")
    void saveRole(Integer user, Integer role);

    @Delete("DELETE FROM user_app_role WHERE user_id = #{user} AND role_id = #{role}")
    void removeRole(Integer user, Integer role);

    @Select("SELECT u.id, u.card_id FROM favorites f, users u WHERE f.user_id = #{id} AND u.id = f.favorite_id \n" +
            "LIMIT #{limit} OFFSET #{offset};")
    @ResultMap(FIND_PROFILE)
    List<Profile> getFavorites(Integer id, Integer limit, Integer offset);

    @Delete("DELETE FROM favorites WHERE user_id = #{user} AND favorite_id = #{favorite}")
    void deleteFavorite(Integer user, Integer favorite);

    @Insert("INSERT INTO favorites(user_id, favorite_id) VALUES (#{user}, #{favorite}) ON CONFLICT DO NOTHING")
    void addFavorite(Integer user, Integer favorite);
}
