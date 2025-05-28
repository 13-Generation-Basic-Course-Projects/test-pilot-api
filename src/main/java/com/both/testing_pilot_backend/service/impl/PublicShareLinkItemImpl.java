package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.PublicShareLinkItem;
import com.both.testing_pilot_backend.repository.PublicShareLinkItemRepository;
import com.both.testing_pilot_backend.service.PublicShareLinkItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShareLinkItemImpl implements PublicShareLinkItemService {
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;

    @Override
    public List<PublicShareLinkItem> getAllPublicShareLinkItems() {
        List<PublicShareLinkItem> linkItems = publicShareLinkItemRepository.getAllPublicShareLinkItems();
        if (linkItems == null || linkItems.isEmpty()) {
            throw new NotFoundException("No public share link items found.");
        }
        return linkItems;
    }

    @Override
    public PublicShareLinkItem getPublicShareLinkItemById(UUID id) {
        PublicShareLinkItem linkItem = publicShareLinkItemRepository.getPublicShareLinkItemById(id);
        if (linkItem == null) {
            throw new NotFoundException("Public share link not found with ID: " + id);
        }
        return linkItem;
    }

    @Override
    public PublicShareLinkItem createPublicShareLinkItem(PublicShareLinkItemRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is missing or invalid.");
        }
        PublicShareLinkItem createdLinkItem = publicShareLinkItemRepository.createPublicShareLinkItem(request);
        if (createdLinkItem == null) {
            throw new BadRequestException("Failed to create public share link item. Please check your input.");
        }
        return createdLinkItem;
    }

    @Override
    public PublicShareLinkItem updatePublicShareLinkItem(UUID id, PublicShareLinkItemRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is missing or invalid.");
        }
        PublicShareLinkItem existingLinkItem = publicShareLinkItemRepository.getPublicShareLinkItemById(id);
        if (existingLinkItem == null) {
            throw new NotFoundException("Public share link item not found with ID: " + id);
        }
        PublicShareLinkItem updatedLinkItem = publicShareLinkItemRepository.updatePublicShareLinkItem(id, request);
        if (updatedLinkItem == null) {
            throw new BadRequestException("Failed to update public share link item.");
        }
        return updatedLinkItem;
    }

    @Override
    public PublicShareLinkItem deletePublicShareLinkItemById(UUID id) {
        PublicShareLinkItem deletedLinkItem = publicShareLinkItemRepository.deletePublicShareLinkItemById(id);
        if (deletedLinkItem == null) {
            throw new NotFoundException("Public share link item not found with ID: " + id);
        }
        return deletedLinkItem;
    }
}
