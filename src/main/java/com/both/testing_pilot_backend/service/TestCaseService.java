// src/main/java/com/both/testing_pilot_backend/service/TestCaseService.java
package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.TestCaseRequest;
import com.both.testing_pilot_backend.model.TestCase;

import java.util.List;
import java.util.UUID;

public interface TestCaseService {
    TestCase createTestCase(TestCaseRequest request);

    TestCase getTestCaseById(UUID id);

    List<TestCase> getAllTestCases();

    List<TestCase> getTestCasesByProjectId(UUID projectId);

    List<TestCase> getPredefinedTestCases();

    TestCase updateTestCase(UUID id, TestCaseRequest request);

    void deleteTestCase(UUID id);

    // This method is now primarily for internal use by TestCaseSecurity, not directly by controller @PreAuthorize
    boolean isTestCaseAuthorized(UUID testCaseId, UUID userId, boolean isAdmin);
}
