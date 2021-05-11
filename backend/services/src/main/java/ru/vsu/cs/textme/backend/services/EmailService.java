package ru.vsu.cs.textme.backend.services;

import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class EmailService {
    private final EmailSender emailSender;
    private final UserMapper emailMapper;
    public EmailService(EmailSender emailSender, UserMapper emailMapper) {
        this.emailSender = emailSender;
        this.emailMapper = emailMapper;
    }

    public void saveInactiveEmail(User user, String email) throws MailException {
        String code = Base64
                .getEncoder()
                .withoutPadding()
                .encodeToString(UUID
                        .randomUUID()
                        .toString()
                        .getBytes(StandardCharsets.UTF_8));
        if (emailMapper.saveInactiveEmail(email, code, user.getId()) > 0)
            emailSender.sendActivateMessage(email, code);
    }

    public User activateEmail(String code) {
        return emailMapper.activateEmail(code);
    }

}
