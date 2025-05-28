package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.repository.PublicShareLinkRepository;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShareLinkImpl implements PublicShareLinkService {
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final AuthUtils authUtils;

    @Override
    public List<PublicShareLink> getAllPublicShareLinks() {
        List<PublicShareLink> links = publicShareLinkRepository.getAllPublicShareLinks();
        if (links == null || links.isEmpty()) {
            throw new NotFoundException("No public share links found.");
        }
        return links;
    }

    @Override
    public PublicShareLink getPublicShareLinkById(UUID id) {
        PublicShareLink link = publicShareLinkRepository.getPublicShareLinkById(id);
        if (link == null) {
            throw new NotFoundException("Public share link not found with ID: " + id);
        }
        return link;
    }

    @Override
    public PublicShareLink createPublicShareLink(PublicShareLinkRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is missing or invalid.");
        }
        PublicShareLink createdLink = publicShareLinkRepository.createPublicShareLink(
            request,
            authUtils.getUserDetails().getUserId()
        );
        if (createdLink == null) {
            throw new BadRequestException("Failed to create public share link. Please check your input.");
        }
        return createdLink;
    }

    @Override
    public PublicShareLink updatePublicShareLink(UUID id, PublicShareLinkRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is missing or invalid.");
        }
        PublicShareLink existingLink = publicShareLinkRepository.getPublicShareLinkById(id);
        if (existingLink == null) {
            throw new NotFoundException("Public share link not found with ID: " + id);
        }
        PublicShareLink updatedLink = publicShareLinkRepository.updatePublicShareLink(id, request);
        if (updatedLink == null) {
            throw new BadRequestException("Failed to update public share link.");
        }
        return updatedLink;
    }

    @Override
    public PublicShareLink deletePublicShareLink(UUID id) {
        PublicShareLink deletedLink = publicShareLinkRepository.deletePublicShareLink(id);
        if (deletedLink == null) {
            throw new NotFoundException("Public share link not found with ID: " + id);
        }
        return deletedLink;
    }
}
