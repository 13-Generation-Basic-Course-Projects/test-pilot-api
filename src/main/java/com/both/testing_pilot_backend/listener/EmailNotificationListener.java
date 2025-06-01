package com.both.testing_pilot_backend.listener;

import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.service.EmailService;
import com.both.testing_pilot_backend.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener {
    private final EmailService emailService;

    public EmailNotificationListener(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleEmailVerification(UserRegistrationEvent payload) {
        emailService.sendRegistrationVerification(payload);
    }

    @EventListener
    public void handlePasswordReset(ForgetPasswordEvent payload) {
        emailService.sendForgetPasswordRequest(payload);
    }

    @EventListener
    public void handleAcceptLinkByEmail(InviteCollaboratorEvent payload){
        emailService.sendAcceptLinkVerification(payload);
    }

}
