package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.event.CollaboratorInvitedEvent;
import com.both.testing_pilot_backend.service.CollaboratorEmailService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CollaboratorEmailServiceImpl implements CollaboratorEmailService {

    // TODO: Inject your actual email sender here

    @Override
    public void sendCollaboratorInviteEmail(CollaboratorInvitedEvent event) {
        String to = event.getCollaboratorEmail();
        String subjectKey = "email.invite.subject";  // For localization/messages.properties
        String templateName = "collaborator-invite"; // Email template file name

        Map<String, Object> variables = new HashMap<>();
        variables.put("projectName", event.getProjectName());
        variables.put("inviterUserId", event.getInviterUserId());
        variables.put("inviteCode", event.getInviteCode());
        variables.put("collaboratorEmail", event.getCollaboratorEmail());

        // TODO: Use your email sender here:
        // emailSender.sendTemplatedEmail(to, subjectKey, templateName, variables);
    }
}
