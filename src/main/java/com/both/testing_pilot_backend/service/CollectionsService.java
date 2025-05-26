package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.CollectionsRequest;
import com.both.testing_pilot_backend.model.Collections;

import java.util.List;
import java.util.UUID;

public interface CollectionsService {

    List<Collections> getAllCollections();

    Collections getCollectionsById(UUID collectionsId);

    Collections saveCollections(CollectionsRequest collectionsRequest);

    Collections updateCollections(Collections collections);

    UUID deleteCollections(UUID collectionsId);

}

