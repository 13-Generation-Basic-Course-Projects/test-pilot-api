package com.both.testing_pilot_backend.service;

import java.util.Map;

public interface EmailSenderService {
    void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
