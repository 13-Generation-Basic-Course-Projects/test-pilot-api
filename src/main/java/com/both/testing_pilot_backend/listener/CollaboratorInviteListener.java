package com.both.testing_pilot_backend.listener;

import com.both.testing_pilot_backend.event.CollaboratorInvitedEvent;
import com.both.testing_pilot_backend.service.CollaboratorEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollaboratorInviteListener {

    private final CollaboratorEmailService collaboratorEmailService;

    @EventListener
    public void onCollaboratorInvited(CollaboratorInvitedEvent event) {
        collaboratorEmailService.sendCollaboratorInviteEmail(event);
    }
}
