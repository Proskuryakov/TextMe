package ru.vsu.cs.textme.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.mapper.EmailMapper;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.*;
import ru.vsu.cs.textme.backend.services.exception.UserAuthException;
import ru.vsu.cs.textme.backend.services.exception.UserExistsException;
import ru.vsu.cs.textme.backend.services.exception.UserForbiddenException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static ru.vsu.cs.textme.backend.db.model.AppRole.ADMIN;
import static ru.vsu.cs.textme.backend.db.model.AppRole.USER;
import static ru.vsu.cs.textme.backend.utils.FileUtils.saveFile;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final AuthService authService;
    private final EmailService emailService;

    public UserService(UserMapper mapper, AuthService authService, EmailService emailService) {
        this.userMapper = mapper;
        this.authService = authService;
        this.emailService = emailService;
    }

    public void register(RegistrationRequest regUser) throws UserExistsException {
        regUser.setPassword(authService.encode(regUser.getPassword()));

        User user = userMapper.createUser(regUser);
        if (user == null)
            throw new UserExistsException("Email or nickname is busy");


        emailService.saveInactiveEmail(user, regUser.getEmail());

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

    public User activateEmail(User user, UUID uuid) {
        String newMail = emailService.activateEmail(uuid, user);
        if (!StringUtils.hasText(newMail))
            throw new UserAuthException("Activation link not exists");
        if (!user.hasRole(USER)) user = userMapper.saveRoleByUserId(user.getId(), USER.getId());
        return user;
    }

    public UserProfileInfo findUserInfoById(int id) {
        return userMapper.findUserInfoById(id);
    }

    public static final String AVATAR = "avatar.jpeg";
    public static final String PHOTOS = "users/%s/photos/";

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
}
