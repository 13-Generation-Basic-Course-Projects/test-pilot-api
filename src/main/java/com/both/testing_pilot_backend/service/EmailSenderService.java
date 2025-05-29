package com.both.testing_pilot_backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface EmailSenderService {
    void sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
