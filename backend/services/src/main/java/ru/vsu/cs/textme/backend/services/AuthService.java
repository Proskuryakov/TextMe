package ru.vsu.cs.textme.backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.model.User;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    public AuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean auth(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    public String encode(String text) {
        return passwordEncoder.encode(text);
    }
}
