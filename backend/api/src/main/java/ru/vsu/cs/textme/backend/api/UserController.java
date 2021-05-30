package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.AuthResponse;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.db.model.request.ChangePasswordRequest;
import ru.vsu.cs.textme.backend.db.model.request.NicknameRequest;
import ru.vsu.cs.textme.backend.db.model.info.Profile;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.security.JwtProvider;
import ru.vsu.cs.textme.backend.services.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtProvider provider;

    public UserController(UserService userService, JwtProvider provider) {
        this.userService = userService;
        this.provider = provider;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Profile getUser(@PathVariable @Valid @Size() int id) {
        return userService.findUserInfoById(id);
    }

    @PostMapping("/nickname")
    public AuthResponse saveContent(@AuthenticationPrincipal CustomUserDetails details,
                            @RequestBody @Valid NicknameRequest nickname) {
        User user = userService.saveUserNickname(details.getUser(), nickname.getName());
        return new AuthResponse(provider.generateToken(user));
    }
    @PostMapping("/pass")
    @ResponseStatus(HttpStatus.OK)
    public void changePass(@RequestBody @Valid @NotNull ChangePasswordRequest request,
                             @AuthenticationPrincipal CustomUserDetails details) {
        userService.changePassword(details.getUser(), request);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Profile getUser(@AuthenticationPrincipal CustomUserDetails d) {
        return userService.findUserInfoById(d.getUser().getId());
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public void setProfileImage(@AuthenticationPrincipal CustomUserDetails details,
                                @RequestParam("image") MultipartFile image) {
        if (image == null) return;
        userService.saveAvatar(image, details.getUsername());
    }

    @GetMapping("/image/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getProfileImage(@PathVariable @Valid @Size() int id) {
        return userService.getAvatarUrl(id);
    }

    @GetMapping("/roles/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<AppRole> getRoles(@AuthenticationPrincipal CustomUserDetails details,
                                  @PathVariable @Valid @Size() int id) {
        return userService.findRoles(details.getUser(), id);
    }
}
