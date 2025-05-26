package com.both.testing_pilot_backend.security.expression;

import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.service.ProjectService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurity {
    private final AuthUtils authUtils;
    private final ProjectService projectService;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;

    public boolean isProjectOwner(UUID projectId) throws AccessDeniedException {
        UUID userId = authUtils.getUserDetails().getUserId();

        if(!projectService.isProjectOwner(projectId, userId)) {
            throw new AccessDeniedException("You are not authorized to access this resource");
        }

        return true;
    }

    /**
     * Checks if the current user is either the owner or a collaborator of a project.
     * @param projectId The ID of the project.
     * @return true if the current user is the owner or a collaborator, false otherwise.
     */
    public boolean isProjectOwnerOrCollaborator(UUID projectId) throws AccessDeniedException {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        // Check if owner
        if (isProjectOwner(projectId)) {
            return true;
        }
        // Check if collaborator
        return projectCollaboratorRepository.isProjectCollaborator(projectId, currentUserId);
    }
}
