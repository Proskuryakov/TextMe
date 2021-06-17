package ru.vsu.cs.textme.backend.services;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    private final JavaMailSender mailSender;
    public static final String AUTH_MAIL = "auth.textme@ya.ru";
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivateMessage(String email, String code) throws MailException {
        var smm = new SimpleMailMessage();
        smm.setFrom(AUTH_MAIL);
        smm.setTo(email);
        smm.setSubject("Activation link");
        smm.setText("http://textme.host/start/activate/" + code);
        try {
            mailSender.send(smm);
        } catch (MailException ignored) {}
    }
}
