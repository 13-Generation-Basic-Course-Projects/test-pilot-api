package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;

import java.util.UUID;

public interface ProjectCollaboratorService {
    void inviteCollaborator(ProjectCollaboratorRequest request);

    // Old method can be removed if unused or kept if needed
    // void verifyCollaboratorInvite(UUID projectCollaboratorId);

    // New method to verify with code
    void verifyCollaboratorInvite(UUID projectCollaboratorId, String code);
}
