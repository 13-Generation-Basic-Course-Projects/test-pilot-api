package com.both.testing_pilot_backend.service;


import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.model.PublicShareLink;

import java.util.List;
import java.util.UUID;

public interface PublicShareLinkService{
    List<PublicShareLink> getAllPublicShareLinks();

    PublicShareLink getPublicShareLinkById(UUID id);

    PublicShareLink createPublicShareLink(PublicShareLinkRequest request);

    PublicShareLink updatePublicShareLink(UUID id, PublicShareLinkRequest request);

    PublicShareLink deletePublicShareLink(UUID id);
}
