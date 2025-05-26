package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.CollectionsRequest;
import com.both.testing_pilot_backend.model.Collections;
import com.both.testing_pilot_backend.repository.CollectionsRepository;
import com.both.testing_pilot_backend.service.CollectionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CollectionsServiceImpl implements CollectionsService {

    private final CollectionsRepository collectionsRepository;

    public CollectionsServiceImpl(CollectionsRepository collectionsRepository) {
        this.collectionsRepository = collectionsRepository;
    }

    @Override
    public List<Collections> getAllCollections() {
        return collectionsRepository.getAllCollections();
    }

    @Override
    public Collections getCollectionsById(UUID collectionsId) {
        return collectionsRepository.getCollectionsById(collectionsId);
    }

    @Override
    @Transactional
    public Collections saveCollections(CollectionsRequest collectionsRequest) {
        return collectionsRepository.saveCollections(collectionsRequest);
    }

    @Override
    @Transactional
    public Collections updateCollections(Collections collections) {
        return collectionsRepository.updateCollections(collections);
    }

    @Override
    @Transactional
    public UUID deleteCollections(UUID collectionsId) {
        return collectionsRepository.deleteCollections(collectionsId);
    }
}