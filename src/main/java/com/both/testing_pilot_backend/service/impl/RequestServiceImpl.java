package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.request.RequestRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.Collection;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.repository.CollectionRepository;
import com.both.testing_pilot_backend.repository.PublicShareLinkItemRepository;
import com.both.testing_pilot_backend.repository.PublicShareLinkRepository;
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.service.RequestService;
import com.both.testing_pilot_backend.security.expression.ProjectSecurity;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final CollectionRepository collectionRepository;
    private final ProjectSecurity projectSecurity;
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final AuthUtils authUtils;
    private final JwtService jwtService;

    @Override
    @Transactional
    public Request createRequest(RequestRequest requestDto) throws java.nio.file.AccessDeniedException {
        Collection collection = collectionRepository.findById(requestDto.getCollectionId());
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + requestDto.getCollectionId());
        }

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

//    @Override
//    public List<Request> getAllRequests() {
//        return requestRepository.findAll();
//    }

    @Override
    public List<Request> getRequestsByCollectionId(UUID collectionId) throws java.nio.file.AccessDeniedException {
        Collection collection = collectionRepository.findById(collectionId);
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

        Collection collection = collectionRepository.findById(existingRequest.getCollectionId());
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + existingRequest.getCollectionId());
        }

        if (!projectSecurity.isProjectOwnerOrCollaborator(collection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to update this request.");
        }

        if (!existingRequest.getCollectionId().equals(requestDto.getCollectionId())) {
            Collection newCollection = collectionRepository.findById(requestDto.getCollectionId());
            if (newCollection == null) {
                throw new NotFoundException("New Collection not found with ID: " + requestDto.getCollectionId());
            }
            if (!projectSecurity.isProjectOwnerOrCollaborator(newCollection.getProjectId())) {
                throw new AccessDeniedException("User is not authorized to move request to the new collection's project.");
            }
        }


        existingRequest.setName(requestDto.getName());
        existingRequest.setCollectionId(requestDto.getCollectionId());
        existingRequest.setMethod(requestDto.getMethod());
        existingRequest.setDetails(requestDto.getDetails());
        existingRequest.setUpdatedAt(LocalDateTime.now());
        existingRequest.setId(requestId);

        return requestRepository.updateRequest(existingRequest);
    }

    @Override
    @Transactional
    public void deleteRequest(UUID requestId) throws java.nio.file.AccessDeniedException {

        Request existingRequest = requestRepository.findById(requestId);
        if (existingRequest == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }

        Collection collection = collectionRepository.findById(existingRequest.getCollectionId());
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + existingRequest.getCollectionId());
        }

        if (!projectSecurity.isProjectOwnerOrCollaborator(collection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to delete this request.");
        }

        requestRepository.deleteById(requestId);
    }

    @Value("${app.dev.frontend.url}")
    private String appBaseUrl;

    @Override
    public String shareLinkByRequestId(List<UUID> requestIds) {
        UUID userSharedId = authUtils.getUserDetails().getUserId();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(7);

        String token = jwtService.generatePublicShareToken(userSharedId, expireAt);
        String verificationLink = String.format("%s/api/v1/publicShareLink/verify?token=%s", appBaseUrl, token);

        for(UUID requestId : requestIds){
            Request request = requestRepository.findById(requestId);
            if(request == null){
                throw new NotFoundException("request cannot be found.");
            }

            PublicShareLinkRequest link = new PublicShareLinkRequest();
            link.setToken(token);
            link.setSharedItemType(request.getName());
            link.setSharedItemId(request.getId());
            link.setExpireAt(expireAt);

            PublicShareLink shareLink = publicShareLinkRepository.createPublicShareLink(link, authUtils.getUserDetails().getUserId());

            PublicShareLinkItemRequest item = new PublicShareLinkItemRequest();
            item.setItemType(request.getName());
            item.setItemId(request.getId());
            item.setShareLinkId(shareLink.getShareLinkId());
            publicShareLinkItemRepository.createPublicShareLinkItem(item);
        }
        return verificationLink;
    }
}
