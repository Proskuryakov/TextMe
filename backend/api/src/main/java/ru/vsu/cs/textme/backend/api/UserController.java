package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.model.UserProfileInfo;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.UserService;

import javax.validation.constraints.NotNull;

import static org.springframework.util.StringUtils.cleanPath;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileInfo getUser(@PathVariable int id) {
        return userService.findUserInfoById(id);
    }

    @PostMapping("/profile/image")
    @ResponseStatus(HttpStatus.OK)
    public void setProfileImage(@AuthenticationPrincipal CustomUserDetails details,
                                @RequestParam("image") MultipartFile image) {
        if (image == null) return;
        userService.saveAvatar(image, details.getUsername());
    }

    @GetMapping("/profile/image/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getProfileImage(@PathVariable int id) {
        return userService.getAvatarUrl(id);
    }
}
