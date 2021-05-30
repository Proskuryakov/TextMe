package ru.vsu.cs.textme.backend.services;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.services.exception.UserAuthException;
import ru.vsu.cs.textme.backend.services.exception.UserExistsException;
import ru.vsu.cs.textme.backend.services.exception.UserForbiddenException;

import java.io.IOException;
import java.util.List;

import static ru.vsu.cs.textme.backend.db.model.AppRole.ADMIN;
import static ru.vsu.cs.textme.backend.db.model.AppRole.USER;
import static ru.vsu.cs.textme.backend.utils.FileUtils.saveFile;

@Service
@Log(topic = "org.slf4j.Logger")
public class UserService {
    private final UserMapper userMapper;
    private final AuthService authService;
    private final EmailService emailService;

    public UserService(UserMapper mapper, AuthService authService, EmailService emailService) {
        this.userMapper = mapper;
        this.authService = authService;
        this.emailService = emailService;
    }

    public User register(RegistrationRequest regUser) throws UserExistsException {
        regUser.setPassword(authService.encode(regUser.getPassword()));

        User user = userMapper.createUser(regUser);
        if (user == null)
            throw new UserExistsException("Email or nickname is busy");


        emailService.saveInactiveEmail(user, regUser.getEmail());
        return user;
    }

    public User findByNickname(String nickname) {
        return userMapper.findUserByNickname(nickname);
    }

    public User authenticate(AuthRequest request) throws UserAuthException {
        User user =
                StringUtils.hasLength(request.getNickname()) ?
                        userMapper.findUserByNickname(request.getNickname()) :
                StringUtils.hasLength(request.getEmail()) ?
                        userMapper.findUserByEmail(request.getEmail()) : null;

        if (user == null)
            throw new UserAuthException("User not found");
        if (!authService.auth(user, request.getPassword()))
            throw new UserAuthException("Invalid password");

        return user;
    }

    public void activateEmail(String code) {
        User user = emailService.activateEmail(code);
        if (user == null)
            throw new UserAuthException("Activation link not exists");
        if (!user.hasRole(USER))
            userMapper.saveRoleByUserId(user.getId(), USER.getId());
    }

    public Profile findUserInfoById(int id) {
        return userMapper.findProfileById(id);
    }

    public static final String AVATAR = "avatar.jpeg";
    public static final String PHOTOS = "users-resources/%s/photos/";

    public void saveAvatar(MultipartFile image, String nickname) {
        userMapper.saveAvatarByNickname(nickname, AVATAR, 0);
        try {
            saveFile(String.format(PHOTOS, nickname), AVATAR, image);
        }
        catch (IOException ignored) {}
    }

    public String getAvatarUrl(int userId) {
        return userMapper.findAvatarByUserId(userId);
    }

    public List<AppRole> findRoles(User user, int userId) {
        if (user.getId() == userId || user.hasRole(ADMIN)) return userMapper.findRolesByUserId(userId);
        throw new UserForbiddenException("Not permissions");
    }

    public void saveUserNickname(User user, String nickname) {
        if (!userMapper.saveNickname(user.getId(), nickname))
            throw new UserForbiddenException("Nickname is busy");
    }
}
