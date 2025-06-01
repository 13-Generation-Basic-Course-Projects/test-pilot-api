package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.model.PublicShareLinkItem;

import java.util.List;
import java.util.UUID;

public interface PublicShareLinkItemService {
    List<PublicShareLinkItem> getAllPublicShareLinkItems();

    PublicShareLinkItem getPublicShareLinkItemById(UUID id);

    PublicShareLinkItem createPublicShareLinkItem(PublicShareLinkItemRequest request);

    PublicShareLinkItem updatePublicShareLinkItem(UUID id, PublicShareLinkItemRequest request);

    PublicShareLinkItem deletePublicShareLinkItemById(UUID id);
}
