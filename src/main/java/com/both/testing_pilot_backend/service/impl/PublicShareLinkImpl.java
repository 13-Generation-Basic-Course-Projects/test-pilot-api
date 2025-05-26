package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.repository.PublicShareLinkRepository;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShareLinkImpl implements PublicShareLinkService {
    private final PublicShareLinkRepository publicShareLinkRepository;

    @Override
    public List<PublicShareLink> getAllPublicShareLinks() {
        return publicShareLinkRepository.getAllPublicShareLinks();
    }

    @Override
    public PublicShareLink getPublicShareLinkById(UUID id) {
        return publicShareLinkRepository.getPublicShareLinkById(id);
    }

    @Override
    public PublicShareLink createPublicShareLink(PublicShareLinkRequest request) {
        return publicShareLinkRepository.createPublicShareLink(request);
    }

    @Override
    public PublicShareLink updatePublicShareLink(UUID id, PublicShareLinkRequest request) {
        return publicShareLinkRepository.updatePublicShareLink(id, request);
    }

    @Override
    public PublicShareLink deletePublicShareLink(UUID id) {
        return publicShareLinkRepository.deletePublicShareLink(id);
    }
}
