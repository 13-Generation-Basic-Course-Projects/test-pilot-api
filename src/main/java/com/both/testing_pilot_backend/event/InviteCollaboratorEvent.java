package com.both.testing_pilot_backend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class InviteCollaboratorEvent extends ApplicationEvent {
    private final String email;
    private final UUID projectCollaboratorId;

    public InviteCollaboratorEvent(Object source, String email, UUID projectCollaboratorId) {
        super(source);
        this.email = email;
        this.projectCollaboratorId = projectCollaboratorId;
    }
}
