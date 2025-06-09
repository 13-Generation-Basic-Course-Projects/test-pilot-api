// src/main/java/com/both/testing_pilot_backend/service/impl/TestCaseServiceImpl.java
package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.TestCaseRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.repository.DataTypeRepository; // To validate dataTypeId
import com.both.testing_pilot_backend.repository.ProjectRepository; // To validate projectId
import com.both.testing_pilot_backend.repository.TestCaseRepository;
import com.both.testing_pilot_backend.service.TestCaseService;
import com.both.testing_pilot_backend.utils.AuthUtils; // Assuming AuthUtils for current user
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final DataTypeRepository dataTypeRepository; // For validating data_type_id
    private final ProjectRepository projectRepository; // For validating project_id

    @Override
    public TestCase createTestCase(TestCaseRequest request) {

        if (dataTypeRepository.findById(request.getDataTypeId()) == null) {
            throw new NotFoundException("DataType not found with ID: " + request.getDataTypeId());
        }

        if (projectRepository.findByProjectId(request.getProjectId()) == null) {
            throw new NotFoundException("Project not found with ID: " + request.getProjectId());
        }

        TestCase testCase = TestCase.builder()
                .projectId(request.getProjectId())
                .dataTypeId(request.getDataTypeId())
                .name(request.getName())
                .value(request.getValue()).isPredefined(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return testCaseRepository.save(testCase);
    }

    @Override
    public TestCase getTestCaseById(UUID id) {
        TestCase testCase = testCaseRepository.findById(id);
        if (testCase == null) {
            throw new NotFoundException("Test case not found with ID: " + id);
        }
        return testCase;
    }

    @Override
    public List<TestCase> getAllTestCases() {
        // This endpoint might need its own security, e.g., only admins can see all test cases
        return testCaseRepository.findAll();
    }

    @Override
    public List<TestCase> getTestCasesByProjectId(UUID projectId) {
        // This method is called after project ownership/collaboration is checked by controller's @PreAuthorize
        if (projectRepository.findByProjectId(projectId) == null) {
            throw new NotFoundException("Project not found with ID: " + projectId);
        }
        return testCaseRepository.findByProjectId(projectId);
    }

    @Override
    public List<TestCase> getPredefinedTestCases() {
        // This endpoint is for reading predefined test cases, accessible to authenticated users
        return testCaseRepository.findPredefinedTestCases();
    }

    @Override
    public TestCase updateTestCase(UUID id, TestCaseRequest request) {
        TestCase existingTestCase = testCaseRepository.findById(id);
        if (existingTestCase == null) {
            throw new NotFoundException("Test case not found with ID: " + id);
        }

        // Service-level check: Predefined test cases cannot be updated via API
        if (existingTestCase.isPredefined()) {
            throw new AccessDeniedException(
                    "Predefined test cases cannot be updated via API. They are managed by scripts.");
        }

        // Validate dataTypeId exists
        if (dataTypeRepository.findById(request.getDataTypeId()) == null) {
            throw new NotFoundException("DataType not found with ID: " + request.getDataTypeId());
        }

        // If not predefined, validate projectId exists
        if (projectRepository.findByProjectId(request.getProjectId()) == null) {
            throw new NotFoundException("Project not found with ID: " + request.getProjectId());
        }

        existingTestCase.setProjectId(request.getProjectId());
        existingTestCase.setDataTypeId(request.getDataTypeId());
        existingTestCase.setName(request.getName());
        existingTestCase.setValue(request.getValue());
        existingTestCase.setPredefined(false);
        existingTestCase.setUpdatedAt(LocalDateTime.now());
        existingTestCase.setId(id); // Ensure ID is set for update query

        return testCaseRepository.update(existingTestCase);
    }

    @Override
    public void deleteTestCase(UUID id) {
        TestCase existingTestCase = testCaseRepository.findById(id);
        if (existingTestCase == null) {
            throw new NotFoundException("Test case not found with ID: " + id);
        }

        // Service-level check: Predefined test cases cannot be deleted via API
        if (existingTestCase.isPredefined()) {
            throw new AccessDeniedException(
                    "Predefined test cases cannot be deleted via API. They are managed by scripts.");
        }

        testCaseRepository.deleteById(id);
    }

    @Override
    public boolean isTestCaseAuthorized(UUID testCaseId, UUID userId, boolean isAdmin) {
        return testCaseRepository.isTestCaseAuthorized(testCaseId, userId, isAdmin);
    }
}
