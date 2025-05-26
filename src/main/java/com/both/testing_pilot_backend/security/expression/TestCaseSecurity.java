package com.both.testing_pilot_backend.security.expression;

import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.repository.ProjectCollaboratorRepository;
import com.both.testing_pilot_backend.service.ProjectService;
import com.both.testing_pilot_backend.service.TestCaseService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component("testCaseSecurity")
@RequiredArgsConstructor
public class TestCaseSecurity {
    private final TestCaseService testCaseService;
    private final ProjectSecurity projectSecurity;

    public boolean canModifyAndDeleteTestCase(UUID testCaseId) throws AccessDeniedException {
        TestCase testCase = testCaseService.getTestCaseById(testCaseId);
        if (testCase == null) {
            // If test case not found, let the service throw NotFoundException later.
            // For security, we'll deny access here.
            return false;
        }

        // THIS IS THE CHECK FOR PREDEFINED TEST CASES:
        if (testCase.isPredefined()) {
            return false; // Predefined test cases cannot be modified/deleted via API
        } else {
            // For custom test cases, check project ownership or collaboration
            return projectSecurity.isProjectOwnerOrCollaborator(testCase.getProjectId());
        }
    }
}
