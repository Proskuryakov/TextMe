package ru.vsu.cs.textme.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.RegistrationRequest;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.UserProfileInfo;
import ru.vsu.cs.textme.backend.services.exception.UserAuthException;
import ru.vsu.cs.textme.backend.services.exception.UserExistsException;

import java.io.IOException;

import static ru.vsu.cs.textme.backend.utils.FileUtils.saveFile;

@Service
public class UserService {
    private final UserMapper mapper;
    private final AuthService authService;

    public UserService(UserMapper mapper, AuthService authService) {
        this.mapper = mapper;
        this.authService = authService;
    }

    public void register(RegistrationRequest registrationUser) throws UserExistsException {
        authService.encode(registrationUser);
        User user = mapper.createUser(registrationUser);
        if (user == null) throw new UserExistsException("Email or nickname is busy");
    }

    public User findByNickname(String nickname) {
        return mapper.findUserByNickname(nickname);
    }

    public User authenticate(String email, String nickname, String password) throws UserAuthException {
        User user = null;
        if (validateString(nickname))
            user = mapper.findUserByNickname(nickname);
        else if (validateEmail(email))
            user = mapper.findUserByEmail(email);

        if (user == null) throw new UserAuthException("User not found");
        if (authService.auth(user ,password)) return user;
        throw new UserAuthException("Invalid password");
    }

    public UserProfileInfo findUserInfoById(int id) {
        return mapper.findUserInfoById(id);
    }

    private boolean validateEmail(String email) {
        return validateString(email);
    }

    private boolean validateString(String nickname) {
        return !(nickname == null || nickname.isEmpty());
    }

    public static final String AVATAR = "avatar.jpeg";
    public static final String PHOTOS = "users/%s/photos/";

    public void saveAvatar(MultipartFile image, String nickname) {
        mapper.saveAvatarByNickname(nickname, AVATAR, 0);
        try {
            saveFile(String.format(PHOTOS, nickname), AVATAR, image);
        }
        catch (IOException ignored) {}
    }

    public String getAvatarUrl(int userId) {
        return mapper.findAvatarByUserId(userId);
    }
}
