package ru.vsu.cs.textme.backend.services;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.EmailMapper;
import ru.vsu.cs.textme.backend.db.model.User;

import java.util.UUID;

@Service
public class EmailService {
    private final EmailSender emailSender;
    private final EmailMapper emailMapper;

    public EmailService(EmailSender emailSender, EmailMapper emailMapper) {
        this.emailSender = emailSender;
        this.emailMapper = emailMapper;
    }

    public void saveInactiveEmail(User user, String email) throws MailException {
        UUID uuid = UUID.randomUUID();
        if (emailMapper.saveInactiveEmail(email, uuid.toString(), user.getId()) > 0)
            emailSender.sendActivateMessage(email, uuid);
    }

    public String activateEmail(UUID uuid, User user) {
        return emailMapper.activateEmail(uuid.toString(), user.getId());
    }

}
