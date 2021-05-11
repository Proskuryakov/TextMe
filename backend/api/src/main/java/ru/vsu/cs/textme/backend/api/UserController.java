package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.cs.textme.backend.db.model.AppRole;
import ru.vsu.cs.textme.backend.db.model.UserProfileInfo;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileInfo getUser(@PathVariable @Valid @Size() int id) {
        return userService.findUserInfoById(id);
    }

    @PostMapping("/nickname")
    public void saveContent(@AuthenticationPrincipal CustomUserDetails details,
                            @RequestBody @Valid @NotEmpty @Size(min=4, max=16) String nickname) {
        userService.saveUserNickname(details.getUser(), nickname);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileInfo getUser(@AuthenticationPrincipal CustomUserDetails d) {
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
