package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.model.ProjectCollaborator;

import java.util.UUID;

public interface ProjectCollaboratorService {
    ProjectCollaborator inviteCollaborator(ProjectCollaboratorRequest request);

    ProjectCollaborator acceptInviteLink(UUID id);
}
