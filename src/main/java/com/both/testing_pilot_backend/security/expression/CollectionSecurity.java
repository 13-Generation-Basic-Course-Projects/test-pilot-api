package com.both.testing_pilot_backend.security.expression;

import com.both.testing_pilot_backend.repository.CollectionRepository;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component("collectionSecurity")
@RequiredArgsConstructor
public class CollectionSecurity {

    private final CollectionRepository collectionRepository;
    private final ProjectSecurity projectSecurity;
    private final AuthUtils authUtils;

    /**
     * Checks if the current user is authorized to create a collection in a given project.
     * Requires ownership or collaboration on the project.
     * @param projectId The ID of the project where the collection will be created.
     * @return true if authorized, false otherwise.
     */
    public boolean canCreateCollectionInProject(UUID projectId) throws AccessDeniedException {
        System.out.println("Add new COllection in herer " + projectId);
        // Project existence check is done in service, this is just permission.
        return projectSecurity.isProjectOwnerOrCollaborator(projectId);
    }

    /**
     * Checks if the current user is authorized to view, update, or delete a specific collection.
     * Requires ownership or collaboration on the collection's parent project.
     * @param collectionId The ID of the collection.
     * @return true if authorized, false otherwise.
     */
    public boolean isCollectionOwnerOrCollaborator(UUID collectionId) {
        // This method fetches the collection to get its projectId.
        // It relies on CollectionRepository.isCollectionOwnerOrCollaborator for the actual DB check.
        // Or, it can fetch the collection and then call projectSecurity.isProjectOwnerOrCollaborator(collection.getProjectId()).
        // Let's use the repository's method directly for efficiency as it's a single DB call.
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        return collectionRepository.isCollectionOwnerOrCollaborator(collectionId, currentUserId);
    }
}
