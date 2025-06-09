package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.RequestRequest;
import com.both.testing_pilot_backend.model.Request;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface RequestService {
    Request createRequest(RequestRequest requestDto) throws AccessDeniedException;
    Request getRequestById(UUID requestId);
//    List<Request> getAllRequests(); // Simplified: no pagination/filters
    List<Request> getRequestsByCollectionId(UUID collectionId) throws AccessDeniedException;
    Request updateRequest(UUID requestId, RequestRequest requestDto) throws AccessDeniedException;
    void deleteRequest(UUID requestId) throws AccessDeniedException;
}
