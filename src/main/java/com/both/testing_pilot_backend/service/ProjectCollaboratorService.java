package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;

import java.util.UUID;

public interface ProjectCollaboratorService {

    void inviteUserToProject(UUID projectId, UUID userId);
}
