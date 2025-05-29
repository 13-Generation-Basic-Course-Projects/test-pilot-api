package com.both.testing_pilot_backend.service;

import java.util.UUID;

public interface ProjectCollaboratorService {

    /**
     * Invite a collaborator by email to a project.
     *
     * @param projectId      the ID of the project
     * @param inviterUserId  the ID of the user sending the invitation
     * @param collaboratorEmail the email of the invited collaborator
     * @return the UUID of the created ProjectCollaborator record
     */
    UUID inviteUserToProject(UUID projectId, UUID inviterUserId, String collaboratorEmail);
}
