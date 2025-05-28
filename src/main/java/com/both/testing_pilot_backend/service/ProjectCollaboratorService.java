package com.both.testing_pilot_backend.service;


import java.util.UUID;

public interface ProjectCollaboratorService {

    UUID inviteUserToProject(UUID projectId, UUID userId);

    boolean isProjectCollaborator(UUID projectId, UUID userId);
}
