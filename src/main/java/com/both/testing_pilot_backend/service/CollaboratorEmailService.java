package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.event.CollaboratorInvitedEvent;

public interface CollaboratorEmailService {
    void sendCollaboratorInviteEmail(CollaboratorInvitedEvent event);
}
