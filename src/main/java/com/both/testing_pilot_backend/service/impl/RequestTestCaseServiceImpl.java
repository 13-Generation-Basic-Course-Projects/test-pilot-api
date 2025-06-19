package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.RequestTestCaseRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.repository.RequestTestCaseRepository;
import com.both.testing_pilot_backend.service.RequestTestCaseService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestTestCaseServiceImpl implements RequestTestCaseService {
    private final RequestTestCaseRepository requestTestCaseRepository;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public RequestTestCase createRequestTestCase(RequestTestCaseRequest request) {
        RequestTestCase requestTestCase = RequestTestCase.builder()
                .requestId(request.getRequestId())
                .testCaseId(request.getTestCaseId())
                .applicationContext(request.getApplicationContext())
                .isExpectedSuccess(request.getIsExpectedSuccess())
                .targetFieldPath(request.getTargetFieldPath())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        return requestTestCaseRepository.save(requestTestCase);
    }

    @Override
    public RequestTestCase getRequestTestCaseById(UUID id) {
        RequestTestCase requestTestCase = requestTestCaseRepository.findById(id);
        if (requestTestCase == null) {
            throw new NotFoundException("Request-Test Case link not found with ID: " + id);
        }
        return requestTestCase;
    }

    @Override
    public List<RequestTestCase> getRequestTestCasesByRequestId(UUID requestId) {
        return requestTestCaseRepository.findByRequestId(requestId);
    }

    @Override
    @Transactional
    public RequestTestCase updateRequestTestCase(UUID id, RequestTestCaseRequest request) {
        RequestTestCase existingLink = requestTestCaseRepository.findById(id);
        if (existingLink == null) {
            throw new NotFoundException("Request-Test Case link not found with ID: " + id);
        }

           if (!existingLink.getRequestId().equals(request.getRequestId()) ||
                !existingLink.getTestCaseId().equals(request.getTestCaseId())) {
            // This scenario would typically involve deleting the old link and creating a new one,
            // or specific business logic for re-linking. For now, treat as immutable to avoid complex unique constraint checks on update.
            throw new IllegalArgumentException("Request ID and Test Case ID cannot be changed for an existing link. Delete and re-create if needed.");
        }

        // Update properties
        existingLink.setApplicationContext(request.getApplicationContext());
        existingLink.setExpectedSuccess(request.getIsExpectedSuccess());
        existingLink.setUpdatedAt(LocalDateTime.now());
        existingLink.setId(id); // Ensure ID is set for update query

        return requestTestCaseRepository.save(existingLink); // Or dedicated update method if distinct from save
    }

    @Override
    @Transactional
    public void deleteRequestTestCase(UUID id) {
        RequestTestCase existingLink = requestTestCaseRepository.findById(id);
        if (existingLink == null) {
            throw new NotFoundException("Request-Test Case link not found with ID: " + id);
        }
        requestTestCaseRepository.deleteById(id);
    }

    @Override
    public boolean isRequestTestCaseAuthorized(UUID requestTestCaseId, UUID userId, boolean isAdmin) {
        return false;
    }

}
