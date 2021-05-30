package ru.vsu.cs.textme.backend.services;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.UserMapper;
import ru.vsu.cs.textme.backend.db.model.User;
import ru.vsu.cs.textme.backend.services.exception.UserException;

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
        String code = getCode(UUID.randomUUID().toString());
        if (emailMapper.saveInactiveEmail(email, code, user.getId()) < 1) return;
        emailSender.sendActivateMessage(email, code);
    }

    public User activateEmail(String code) {
        return emailMapper.activateEmail(code);
    }

    public void resendEmail(User user, String email) {
        String uuid = emailMapper.getInactiveUuid(user.getId(), email);
        if (uuid == null) throw new UserException("EMAIL NOT FOUND");
        emailSender.sendActivateMessage(email, getCode(uuid));
    }

    public String getCode(String uuid) {
        return  Base64
                .getEncoder()
                .withoutPadding()
                .encodeToString(uuid.getBytes(StandardCharsets.UTF_8));
    }
}
