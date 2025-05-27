package com.both.testing_pilot_backend.security.expression;// src/main/java/com/both/testing_pilot_backend/security/RequestTestCaseSecurity.java

import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.repository.RequestTestCaseRepository;
import com.both.testing_pilot_backend.security.expression.ProjectSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component("requestTestCaseSecurity")
@RequiredArgsConstructor
public class RequestTestCaseSecurity {

    private final RequestTestCaseRepository requestTestCaseRepository;
    private final ProjectSecurity projectSecurity;
    public boolean isAuthorized(UUID requestTestCaseId) throws AccessDeniedException {
        RequestTestCase link = requestTestCaseRepository.findById(requestTestCaseId);
        if (link == null) {
            return false;
        }
        return projectSecurity.isProjectOwnerOrCollaborator(link.getRequest().getProjectId()); // Access project ID from the linked request object
    }


    public boolean canCreateLinkForRequest(UUID requestId) throws AccessDeniedException {
        return projectSecurity.isProjectOwnerOrCollaborator(requestId);
    }
}
