package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.RequestTestCaseRequest;
import com.both.testing_pilot_backend.model.RequestTestCase;

import java.util.List;
import java.util.UUID;

public interface RequestTestCaseService {
    RequestTestCase createRequestTestCase(RequestTestCaseRequest request);
    RequestTestCase getRequestTestCaseById(UUID id);
    List<RequestTestCase> getRequestTestCasesByRequestId(UUID requestId);
    RequestTestCase updateRequestTestCase(UUID id, RequestTestCaseRequest request);
    void deleteRequestTestCase(UUID id);
    boolean isRequestTestCaseAuthorized(UUID requestTestCaseId, UUID userId, boolean isAdmin);
}
