package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true means HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendRegistrationVerification(UserRegistrationEvent event) {
        String to = event.getEmail();
        String subject = "Please verify your registration";
        String htmlBody = "<p>Click the link to verify your account...</p>";
        sendHtmlEmail(to, subject, htmlBody);
    }

    @Override
    public void sendForgetPasswordRequest(ForgetPasswordEvent event) {
        String to = event.getOtpCode(); // corrected here
        String subject = "Password Reset Request";
        String htmlBody = "<p>Click the link to reset your password...</p>";
        sendHtmlEmail(to, subject, htmlBody);
    }
}
