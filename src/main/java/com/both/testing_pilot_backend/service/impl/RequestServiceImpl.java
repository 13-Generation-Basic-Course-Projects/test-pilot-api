// src/main/java/com/both/testing_pilot_backend/service/impl/RequestServiceImpl.java
package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.RequestRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.Collections; // Import Collection model
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.repository.CollectionsRepository; // Import CollectionRepository
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.service.RequestService;
import com.both.testing_pilot_backend.security.expression.ProjectSecurity; // Import ProjectSecurity for defensive checks
import com.both.testing_pilot_backend.utils.AuthUtils; // Import AuthUtils
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transactional operations

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final CollectionsRepository collectionRepository; // Injected
    private final ProjectSecurity projectSecurity; // Injected for defensive checks
    private final AuthUtils authUtils; // Injected for user ID

    @Override
    @Transactional
    public Request createRequest(RequestRequest requestDto) throws java.nio.file.AccessDeniedException {
        // 1. Fetch Collection to get projectId
        Collections collection = collectionRepository.getCollectionsById(requestDto.getCollectionId());
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + requestDto.getCollectionId());
        }

        // 2. Defensive Security Check (redundant if @PreAuthorize is perfect, but good practice)
        if (!projectSecurity.isProjectOwnerOrCollaborator(collection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to create requests in this collection's project.");
        }

        Request request = Request.builder()
                .name(requestDto.getName())
                .collectionId(requestDto.getCollectionId())
                .method(requestDto.getMethod())
                .details(requestDto.getDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectId(collection.getProjectId()) // Set projectId from the collection
                .build();
        return requestRepository.save(request);
    }

    @Override
    public Request getRequestById(UUID requestId) {
        Request request = requestRepository.findById(requestId);
        if (request == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }
        return request;
    }

    @Override
    public List<Request> getAllRequests() {
        // This method might need security/filtering if not all requests should be visible to all users
        return requestRepository.findAll();
    }

    @Override
    public List<Request> getRequestsByCollectionId(UUID collectionId) throws java.nio.file.AccessDeniedException {
        // Defensive security check
        Collections collection = collectionRepository.getCollectionsById(collectionId);
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + collectionId);
        }
        if (!projectSecurity.isProjectOwnerOrCollaborator(collection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to view requests in this collection's project.");
        }
        return requestRepository.findByCollectionId(collectionId);
    }

    @Override
    @Transactional
    public Request updateRequest(UUID requestId, RequestRequest requestDto) throws java.nio.file.AccessDeniedException {
        Request existingRequest = requestRepository.findById(requestId);
        if (existingRequest == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }

        // Defensive security check (redundant if @PreAuthorize is perfect, but good practice)
        if (!projectSecurity.isProjectOwnerOrCollaborator(existingRequest.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to update this request.");
        }

        // If collectionId is changed, validate new collection and its project access
        if (!existingRequest.getCollectionId().equals(requestDto.getCollectionId())) {
            Collections newCollection = collectionRepository.getCollectionsById(requestDto.getCollectionId());
            if (newCollection == null) {
                throw new NotFoundException("New Collection not found with ID: " + requestDto.getCollectionId());
            }
            if (!projectSecurity.isProjectOwnerOrCollaborator(newCollection.getProjectId())) {
                throw new AccessDeniedException("User is not authorized to move request to the new collection's project.");
            }
            // Update projectId if collection changes
            existingRequest.setProjectId(newCollection.getProjectId());
        }


        existingRequest.setName(requestDto.getName());
        existingRequest.setCollectionId(requestDto.getCollectionId());
        existingRequest.setMethod(requestDto.getMethod());
        existingRequest.setDetails(requestDto.getDetails());
        existingRequest.setUpdatedAt(LocalDateTime.now());
        existingRequest.setId(requestId); // Ensure ID is set for update query

        return requestRepository.updateRequest(existingRequest);
    }

    @Override
    @Transactional
    public void deleteRequest(UUID requestId) throws java.nio.file.AccessDeniedException {
        Request existingRequest = requestRepository.findById(requestId);
        if (existingRequest == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }

        // Defensive security check (redundant if @PreAuthorize is perfect, but good practice)
        if (!projectSecurity.isProjectOwnerOrCollaborator(existingRequest.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to delete this request.");
        }

        requestRepository.deleteById(requestId);
    }
}
