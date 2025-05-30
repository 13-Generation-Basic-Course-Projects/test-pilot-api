package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.event.UserRegistrationEvent;
import com.both.testing_pilot_backend.event.ForgetPasswordEvent;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlBody);
    void sendRegistrationVerification(UserRegistrationEvent event);
    void sendForgetPasswordRequest(ForgetPasswordEvent event);
}
