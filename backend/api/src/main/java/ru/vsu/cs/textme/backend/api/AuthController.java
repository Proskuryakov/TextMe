package ru.vsu.cs.textme.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.security.JwtProvider;
import ru.vsu.cs.textme.backend.db.model.request.AuthRequest;
import ru.vsu.cs.textme.backend.db.model.AuthResponse;
import ru.vsu.cs.textme.backend.db.model.request.RegistrationRequest;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.services.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse auth(@RequestBody AuthRequest request) {
        User user = userService.authenticate(request);
        String token = jwtProvider.generateToken(user);
        return new AuthResponse(token);
    }

    @GetMapping("/activate/{code}")
    @ResponseStatus(HttpStatus.OK)
    public void activateEmail(@PathVariable String code) {
        userService.activateEmail(code);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody @Valid RegistrationRequest request) {
        userService.register(request);
    }

    @PostMapping("/activate/resend")
    @ResponseStatus(HttpStatus.OK)
    public void resendMail(@RequestBody @Valid RegistrationRequest request) {
        userService.resendMail(request);
    }
}
