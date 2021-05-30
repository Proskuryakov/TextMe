package ru.vsu.cs.textme.backend.services;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.db.model.request.AuthRequest;
import ru.vsu.cs.textme.backend.db.model.request.ChangePasswordRequest;
import ru.vsu.cs.textme.backend.db.model.request.RegistrationRequest;
import ru.vsu.cs.textme.backend.services.exception.UserException;
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

    public void changePassword(User user, ChangePasswordRequest request) {
        if (request.isSimilar())  throw new UserException("SIMILAR PASSWORDS");
        if (!authService.auth(user, request.getOld()))  throw new UserException("BAD PASSWORD");
        String encoded = authService.encode(request.getChange());
        userMapper.savePassword(user.getId(), encoded);
    }

    public User register(RegistrationRequest regUser) throws UserException {
        regUser.setPassword(authService.encode(regUser.getPassword()));

        User user = userMapper.createUser(regUser);
        if (user == null)
            throw new UserException("EMAIL OR NAME IS BUSY");


        emailService.saveInactiveEmail(user, regUser.getEmail());
        return user;
    }

    public void resendMail(RegistrationRequest request) {
        User user = userMapper.findUserByEmail(request.getEmail());
        if (user == null) throw new UserException("EMAIL NOT FOUND");
        if (user.getNickname().equals(request.getNickname())) throw new UserException("WRONG NAME");
        if (authService.auth(user, request.getPassword())) throw new UserException("WRONG PASS");

        emailService.resendEmail(user, request.getEmail());
    }

    public User findByNickname(String nickname) {
        return userMapper.findUserByNickname(nickname);
    }

    public User authenticate(AuthRequest request) throws UserException {
        User user =
                StringUtils.hasLength(request.getNickname()) ?
                        userMapper.findUserByNickname(request.getNickname()) :
                StringUtils.hasLength(request.getEmail()) ?
                        userMapper.findUserByEmail(request.getEmail()) : null;

        if (user == null)
            throw new UserException("USER NOT FOUND");
        if (!authService.auth(user, request.getPassword()))
            throw new UserException("INVALID PASS");

        return user;
    }

    public void activateEmail(String code) {
        User user = emailService.activateEmail(code);
        if (user == null)
            throw new UserException("LINK NOT EXISTS");
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
        throw new UserForbiddenException("NOT PERMS");
    }

    public User saveUserNickname(User user, String nickname) {
        if (!userMapper.saveNickname(user.getId(), nickname))
            throw new UserForbiddenException("NAME IS BUSY");
        user.setNickname(nickname);
        return user;
    }

}
