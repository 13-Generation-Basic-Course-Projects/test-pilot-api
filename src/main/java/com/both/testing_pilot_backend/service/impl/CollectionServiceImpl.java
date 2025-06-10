package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.CollectionRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.Collection;
import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.security.expression.ProjectSecurity;
import com.both.testing_pilot_backend.service.CollectionService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepository collectionRepository;
    private final ProjectRepository projectRepository;
    private final ProjectSecurity projectSecurity;
    private final RequestRepository requestRepository;
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;
    private final AuthUtils authUtils;

    @Override
    public List<Collection> getCollectionsByProjectId(UUID projectId) {
        return collectionRepository.findByProjectId(projectId);
    }

    @Override
    public Collection getCollectionById(UUID id) {
        Collection collection = collectionRepository.findById(id);
        if (collection == null) {
            throw new NotFoundException("Collection not found with ID: " + id);
        }
        return collection;
    }

    @Override
    @Transactional
    public Collection createCollection(CollectionRequest request) throws java.nio.file.AccessDeniedException {
        Project project = projectRepository.findByProjectId(request.getProjectId());
        if (project == null) {
            throw new NotFoundException("Project not found with ID: " + request.getProjectId());
        }

        if (!projectSecurity.isProjectOwnerOrCollaborator(request.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to create collections in this project.");
        }

        Collection collection = Collection.builder()
                .name(request.getName())
                .projectId(request.getProjectId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        try {
            return collectionRepository.save(collection);
        } catch (DuplicateKeyException e) {
            String errorMessage = String.format(
                    "Collection with name '%s' already exists in project '%s'.",
                    request.getName(), request.getProjectId()
            );
            throw new DuplicateKeyException(errorMessage, e);
        }
    }

    @Override
    @Transactional
    public Collection updateCollection(UUID id, CollectionRequest request) throws java.nio.file.AccessDeniedException {
        Collection existingCollection = collectionRepository.findById(id);
        if (existingCollection == null) {
            throw new NotFoundException("Collection not found with ID: " + id);
        }

        if (!projectSecurity.isProjectOwnerOrCollaborator(existingCollection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to update this collection.");
        }

        if (!existingCollection.getProjectId().equals(request.getProjectId())) {
            Project newProject = projectRepository.findByProjectId(request.getProjectId());
            if (newProject == null) {
                throw new NotFoundException("New Project not found with ID: " + request.getProjectId());
            }
            if (!projectSecurity.isProjectOwnerOrCollaborator(request.getProjectId())) {
                throw new AccessDeniedException("User is not authorized to move collection to the new project.");
            }
        }

        existingCollection.setName(request.getName());
        existingCollection.setProjectId(request.getProjectId());
        existingCollection.setUpdatedAt(LocalDateTime.now());
        existingCollection.setId(id);

        try {
            return collectionRepository.update(existingCollection);
        } catch (DuplicateKeyException e) {
            String errorMessage = String.format(
                    "Cannot update collection: A collection with name '%s' already exists in project '%s'.",
                    request.getName(), request.getProjectId()
            );
            throw new DuplicateKeyException(errorMessage, e);
        }
    }

    @Override
    @Transactional
    public void deleteCollection(UUID id) throws java.nio.file.AccessDeniedException {
        Collection existingCollection = collectionRepository.findById(id);
        if (existingCollection == null) {
            throw new NotFoundException("Collection not found with ID: " + id);
        }

        if (!projectSecurity.isProjectOwnerOrCollaborator(existingCollection.getProjectId())) {
            throw new AccessDeniedException("User is not authorized to delete this collection.");
        }

        collectionRepository.softDeleteById(id);
    }

    @Override
    public boolean isCollectionOwnerOrCollaborator(UUID collectionId, UUID userId) {
        return collectionRepository.isCollectionOwnerOrCollaborator(collectionId, userId);
    }

    @Override
    public String shareLinkByCollectionId(List<UUID> collectionIds) {

        String token = String.valueOf(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(7);

        for(UUID collectionId : collectionIds){
            Collection existCollection =  collectionRepository.findById(collectionId);
            List<Request> requests = requestRepository.findByCollectionId(existCollection.getId());

            PublicShareLinkRequest link = new PublicShareLinkRequest();
            link.setToken(token);
            link.setSharedItemType(existCollection.getName());
            link.setSharedItemId(existCollection.getId());
            link.setExpireAt(expireAt);

            PublicShareLink shareLink = publicShareLinkRepository.createPublicShareLink(link, authUtils.getUserDetails().getUserId());

            List<PublicShareLinkItemRequest> items = requests.stream()
                .map(req -> {
                    PublicShareLinkItemRequest item = new PublicShareLinkItemRequest();
                    item.setItemType(req.getName());
                    item.setItemId(req.getId());
                    item.setShareLinkId(shareLink.getShareLinkId());
                    publicShareLinkItemRepository.createPublicShareLinkItem(item);
                    return item;
                })
                .toList();
        }
        return token;
    }
}
