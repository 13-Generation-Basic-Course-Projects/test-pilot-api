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
    private final AuthUtils authUtils;
    private final PublicShareLinkRepository publicShareLinkRepository;

    @Override
    public List<PublicShareLink> getAllPublicShareLinks() {
        List<PublicShareLink> links = publicShareLinkRepository.getAllPublicShareLinks();
        if (links.isEmpty()) {
            throw new NotFoundException("No public share links found.");
        }
        return links;
    }

    @Override
    public PublicShareLink getPublicShareLinkById(UUID id) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        PublicShareLink link = publicShareLinkRepository.getPublicShareLinkById(id);
        if (link == null){
            throw new NotFoundException("Public share link not found with ID:" + id);
        }
        return link;
    }

    @Override
    public PublicShareLink createPublicShareLink(PublicShareLinkRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        UUID userId = authUtils.getUserDetails().getUserId();
        if (userId == null) {
            throw new BadRequestException("User must be authenticated to create a public share link.");
        }

        return publicShareLinkRepository.createPublicShareLink(request, authUtils.getUserDetails().getUserId());
    }

    @Override
    public PublicShareLink updatePublicShareLink(UUID id, PublicShareLinkRequest request) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        PublicShareLink existingLink = publicShareLinkRepository.getPublicShareLinkById(id);
        if (existingLink == null) {
            throw new NotFoundException("Public share link not found with ID: " + id);
        }
        return publicShareLinkRepository.updatePublicShareLink(id, request);
    }

    @Override
    public PublicShareLink deletePublicShareLink(UUID id) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        PublicShareLink deletedLink = publicShareLinkRepository.deletePublicShareLink(id);
        if (deletedLink == null){
            throw new NotFoundException("Public share link not found with ID:" + id);
        }
        return deletedLink;
    }
}
