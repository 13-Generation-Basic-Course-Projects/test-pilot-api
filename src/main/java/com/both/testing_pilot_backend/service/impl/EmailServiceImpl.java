package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;
    @Qualifier("postmarkWebClient")
    private final WebClient postmarkWebClient;

    @Value("${spring.mail.username}")
    private String sendFrom;

    @Value("${spring.mail.support_email}")
    private String supportEmail;

    @Value("${postmark.api.token}")
    private String postmarkApiToken;

    @Override
    public void sendRegistrationVerification(UserRegistrationEvent payload) {
        String sendTo = payload.getEmail();
        String subject = "Testing Email";
        String templateName = "email-verification";

        Map<String, Object> variables = new HashMap<>();
        variables.put("logoUrl",
                "https://lh3.googleusercontent.com/a/ACg8ocI6Xb97ga-IEBsn-AcJrrhJmXlzXki5xBOyzLD38kMQTU3Uo1E=s288-c-no");
        variables.put("companyName", "TestingPilot");
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
        variables.put("logoUrl",
                "https://lh3.googleusercontent.com/a/ACg8ocI6Xb97ga-IEBsn-AcJrrhJmXlzXki5xBOyzLD38kMQTU3Uo1E=s288-c-no");
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
        sendHtmlEmailViaPostmark(sendTo, subject, htmlBody);
    }

    private void sendHtmlEmailViaPostmark(String sendTo, String subject, String htmlBody) {
        // Create Postmark email request body
        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("From", sendFrom);
        emailRequest.put("To", sendTo);
        emailRequest.put("Subject", subject);
        emailRequest.put("HtmlBody", htmlBody);
        emailRequest.put("MessageStream", "outbound");

        // Send email via Postmark API
        postmarkWebClient.post().uri("/email").bodyValue(emailRequest).retrieve().bodyToMono(String.class).doOnSuccess(
                response -> {
                    log.info("Email sent successfully to: {}", sendTo);
                    log.debug("Postmark response: {}", response);
                }).doOnError(WebClientResponseException.class, error -> {
            log.error("Failed to send email to: {}. Status: {}. Response: {}",
                    sendTo,
                    error.getStatusCode(),
                    error.getResponseBodyAsString());
        }).onErrorResume(throwable -> {
            // This catches other exceptions besides WebClientResponseException
            log.error("Email sending failed with an unexpected exception", throwable);
            return Mono.empty();
        }).subscribe(); // Non-blocking execution
    }

    @Override
    public void sendCollaboratorInvite(InviteCollaboratorEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("invitedUserName", event.getInvitedUserName());
        variables.put("invitingUserName", event.getInvitingUserName());
        variables.put("projectName", event.getProjectName());
        variables.put("verificationLink", event.getVerificationLink());

        variables.put("companyAddress", "KHSRD Center");
        variables.put("supportEmail", supportEmail);
        variables.put("logoUrl",
                "https://lh3.googleusercontent.com/a/ACg8ocI6Xb97ga-IEBsn-AcJrrhJmXlzXki5xBOyzLD38kMQTU3Uo1E=s288-c-no");
        variables.put("year", LocalDateTime.now().getYear());

        sendTemplatedEmail(event.getCollaboratorEmail(),
                "You're Invited to Collaborate!",
                "invite-collaborator",
                variables);
    }
}