package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.event.ForgetPasswordEvent;

import java.util.Map;

public interface EmailService {
    void sendRegistrationVerification(UserRegistrationEvent payload);
    void sendForgetPasswordRequest(ForgetPasswordEvent payload);
    void sendTemplatedEmail(String sendTo, String subject, String templateName, Map<String, Object> variables);
    void sendAcceptLinkVerification(InviteCollaboratorEvent payload);
    void sendHtmlEmail(String sendTo, String subject, String htmlBody);
}
