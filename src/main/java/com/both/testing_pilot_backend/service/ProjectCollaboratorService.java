package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.ProjectCollaboratorDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectCollaboratorService {
    void inviteCollaborator(ProjectCollaboratorRequest request);

    void verifyCollaboratorInvite(String verificationToken);

    void deleteCollaborator(UUID projectCollaboratorId);

    List<ProjectCollaboratorDTO> getCollaboratorByProjectId(UUID projectId);
}
