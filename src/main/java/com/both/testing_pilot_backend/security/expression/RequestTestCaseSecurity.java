package com.both.testing_pilot_backend.security.expression;

import com.both.testing_pilot_backend.model.Collection;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.repository.CollectionRepository;
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.repository.RequestTestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component("requestTestCaseSecurity")
@RequiredArgsConstructor
public class RequestTestCaseSecurity {

    private final RequestTestCaseRepository requestTestCaseRepository;
    private final RequestRepository requestRepository;
    private final CollectionRepository collectionsRepository;
    private final ProjectSecurity projectSecurity;

    /**
     * Checks if the current user is authorized to perform an action on a specific RequestTestCase link.
     * Authorization is based on the parent request's project ownership/collaboration.
     * @param requestTestCaseId The ID of the RequestTestCase link.
     * @return true if authorized, false otherwise.
     */
    public boolean isAuthorized(UUID requestTestCaseId) throws AccessDeniedException {
        RequestTestCase link = requestTestCaseRepository.findById(requestTestCaseId);
        if (link == null) {
            // This case will be handled by NotFoundException from service, but security denies access if not found.
            return false;
        }
        if (link.getRequest() == null) {
            return false;
        }
        return projectSecurity.isProjectOwnerOrCollaborator(link.getRequest().getProjectId());
    }

    /**
     * Checks if the current user is authorized to create a RequestTestCase link for a given Request.
     * Requires ownership or collaboration on the Request's project.
     * @param requestId The ID of the Request to link to.
     * @return true if authorized, false otherwise.
     */
    public boolean canCreateLinkForRequest(UUID requestId) throws AccessDeniedException {
        System.out.println("working herherher " + requestId );
        // 1. Fetch the Request to get its projectId
        Request request = requestRepository.findById(requestId);
        System.out.println("Request  " + request);
        if (request == null) {
            return false;
        }

        Collection collections = collectionsRepository.findById(request.getCollectionId());

        System.out.println("working in project collaborator ");
        return projectSecurity.isProjectOwnerOrCollaborator(collections.getProjectId());
    }
}
