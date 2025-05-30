package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.event.ForgetPasswordEvent;
import com.both.testing_pilot_backend.event.UserRegistrationEvent;

import java.util.Map;

public interface EmailService {
    void sendRegistrationVerification(UserRegistrationEvent payload);
    void sendForgetPasswordRequest(ForgetPasswordEvent payload);
    void sendTemplatedEmail(String to, String subjectKey, String templateName, Map<String, Object> variables);
}