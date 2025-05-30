package com.both.testing_pilot_backend.listener;

import com.both.testing_pilot_backend.event.InviteCollaboratorEvent;
import com.both.testing_pilot_backend.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class InviteCollaboratorListener {

    private final EmailSenderService emailSenderService;

    @EventListener
    public void handleInvite(InviteCollaboratorEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("projectCollaboratorId", event.getProjectCollaboratorId().toString());
        variables.put("verificationCode", event.getVerificationCode());

        emailSenderService.sendTemplatedEmail(
                event.getEmail(),
                "You are invited to collaborate!",
                "invite-template",
                variables
        );
    }

}

