package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sendFrom;

    @Value("${spring.mail.support_email}")
    private String supportEmail;

    public EmailServiceImpl(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = emailSender;
        this.templateEngine = templateEngine;
    }


    @Override
    public void sendRegistrationVerification(UserRegistrationEvent payload) {
        String sendTo = payload.getEmail();
        String subject = "Testing Email";
        String templateName = "email-verification";

        Map<String, Object> variables = new HashMap<>();
        variables.put("logoUrl", "https://lh3.googleusercontent.com/a/ACg8ocI6Xb97ga-IEBsn-AcJrrhJmXlzXki5xBOyzLD38kMQTU3Uo1E=s288-c-no");
        variables.put("companyName", "TestingPIlot");
        variables.put("name", payload.getName());
        variables.put("expiration", "7 days");
        variables.put("otpCode", payload.getOtpCode());
        variables.put("supportEmail", supportEmail);
        variables.put("year", 2025);
        variables.put("companyAddress", "KHSRD Center");

        sendTemplatedEmail(sendTo, subject, templateName, variables);
    }

    @Override
    public void sendForgetPasswordRequest(ForgetPasswordEvent payload) {
        String sendTo = payload.getUser().getEmail();
        String subject = "Forget Password Request";
        String templateName = "forget-password-request";

        Map<String, Object> variables = new HashMap<>();
        variables.put("logoUrl", "https://lh3.googleusercontent.com/a/ACg8ocI6Xb97ga-IEBsn-AcJrrhJmXlzXki5xBOyzLD38kMQTU3Uo1E=s288-c-no");
        variables.put("companyName", "TestingPilot");
        variables.put("name", payload.getUser().getUsername());
        variables.put("expiration", "7 days");
        variables.put("otpCode", payload.getOtpCode());
        variables.put("supportEmail", supportEmail);
        variables.put("year", 2025);
        variables.put("companyAddress", "KHSRD Center");

        sendTemplatedEmail(sendTo, subject, templateName, variables);
    }

    @Override
    public void sendTemplatedEmail(String sendTo, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String htmlBody = templateEngine.process(templateName, context);
        sendHtmlEmail(sendTo, subject, htmlBody);
    }

    private void sendHtmlEmail(String sendTo, String subject, String htmlBody) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
            helper.setFrom(sendFrom);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML content

            mailSender.send(mimeMessage);
            System.out.println("Already sent");

        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        } catch (MailException e) { // Catch specific Spring Mail exceptions
            System.out.println(e.getMessage());
        }
    }
}