// src/main/java/com/both/testing_pilot_backend/service/CollectionService.java
package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.CollectionRequest;
import com.both.testing_pilot_backend.model.Collection;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface CollectionService {
    String shareLinkByCollectionId(List<UUID> collectionId);

    List<Collection> getCollectionsByProjectId(UUID projectId);

    Collection getCollectionById(UUID id); 

    Collection createCollection(CollectionRequest request) throws AccessDeniedException;

    Collection updateCollection(UUID id, CollectionRequest request) throws AccessDeniedException;

    void deleteCollection(UUID id) throws AccessDeniedException;

    boolean isCollectionOwnerOrCollaborator(UUID collectionId, UUID userId);
}
