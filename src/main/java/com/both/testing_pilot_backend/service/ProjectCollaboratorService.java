package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;

import java.util.UUID;

public interface ProjectCollaboratorService {
    void inviteCollaborator(ProjectCollaboratorRequest request);
    void verifyCollaboratorInvite(UUID projectCollaboratorId);
}
