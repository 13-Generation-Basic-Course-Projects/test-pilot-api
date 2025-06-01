package com.both.testing_pilot_backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class InviteCollaboratorEvent extends ApplicationEvent {
    private final String email;
    private final UUID projectCollaboratorId;
    private final String acceptLink;

    public InviteCollaboratorEvent(Object source, String email, UUID projectCollaboratorId, String acceptLink) {
        super(source);
        this.email = email;
        this.projectCollaboratorId = projectCollaboratorId;
        this.acceptLink = acceptLink;
    }
}


