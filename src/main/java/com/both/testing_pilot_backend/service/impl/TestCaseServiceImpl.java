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
    private final AuthUtils authUtils; // For security checks

    @Override
    public TestCase createTestCase(TestCaseRequest request) {
        // Validate dataTypeId exists
        if (dataTypeRepository.findById(request.getDataTypeId()) == null) {
            throw new NotFoundException("DataType not found with ID: " + request.getDataTypeId());
        }

        // If not predefined, validate projectId exists
        if (!request.isPredefined() && projectRepository.findByProjectId(request.getProjectId()) == null) {
            throw new NotFoundException("Project not found with ID: " + request.getProjectId());
        }

        TestCase testCase = TestCase.builder()
                .projectId(request.getProjectId())
                .dataTypeId(request.getDataTypeId())
                .name(request.getName())
                .value(request.getValue())
                .isPredefined(request.isPredefined())
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
        return testCaseRepository.findAll();
    }

    @Override
    public List<TestCase> getTestCasesByProjectId(UUID projectId) {
        // Optional: Add project existence check and security check here
        if (projectRepository.findByProjectId(projectId) == null) {
            throw new NotFoundException("Project not found with ID: " + projectId);
        }
        // Ensure user has access to this project
        // if (!authUtils.isProjectOwner(projectId, authUtils.getUserDetails().getUserId())) {
        //     throw new AccessDeniedException("User not authorized to view test cases for this project.");
        // }
        return testCaseRepository.findByProjectId(projectId);
    }

    @Override
    public List<TestCase> getPredefinedTestCases() {
        return testCaseRepository.findPredefinedTestCases();
    }

    @Override
    public TestCase updateTestCase(UUID id, TestCaseRequest request) {
        TestCase existingTestCase = testCaseRepository.findById(id);
        if (existingTestCase == null) {
            throw new NotFoundException("Test case not found with ID: " + id);
        }

        // Validate dataTypeId exists
        if (dataTypeRepository.findById(request.getDataTypeId()) == null) {
            throw new NotFoundException("DataType not found with ID: " + request.getDataTypeId());
        }

        // If not predefined, validate projectId exists
        if (!request.isPredefined() && projectRepository.findByProjectId(request.getProjectId()) == null) {
            throw new NotFoundException("Project not found with ID: " + request.getProjectId());
        }

        existingTestCase.setProjectId(request.getProjectId());
        existingTestCase.setDataTypeId(request.getDataTypeId());
        existingTestCase.setName(request.getName());
        existingTestCase.setValue(request.getValue());
        existingTestCase.setPredefined(request.isPredefined()); // Corrected: Changed to setPredefined()
        existingTestCase.setUpdatedAt(LocalDateTime.now());
        existingTestCase.setId(id); // Ensure ID is set for update query

        return testCaseRepository.update(existingTestCase);
    }

    @Override
    public void deleteTestCase(UUID id) {
        if (testCaseRepository.findById(id) == null) {
            throw new NotFoundException("Test case not found with ID: " + id);
        }
        testCaseRepository.deleteById(id);
    }

    @Override
    public boolean isTestCaseAuthorized(UUID testCaseId, UUID userId, boolean isAdmin) {
        return testCaseRepository.isTestCaseAuthorized(testCaseId, userId, isAdmin);
    }
}