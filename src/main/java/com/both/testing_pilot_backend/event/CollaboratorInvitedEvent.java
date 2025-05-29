package com.both.testing_pilot_backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CollaboratorInvitedEvent extends ApplicationEvent {
    private final String collaboratorEmail;
    private final String projectName;
    private final String inviteCode;
    private final String inviterUserId;

    public CollaboratorInvitedEvent(Object source, String collaboratorEmail, String projectName, String inviteCode, String inviterUserId) {
        super(source);
        this.collaboratorEmail = collaboratorEmail;
        this.projectName = projectName;
        this.inviteCode = inviteCode;
        this.inviterUserId = inviterUserId;
    }
}
