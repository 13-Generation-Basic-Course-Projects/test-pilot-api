package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.repository.PublicShareLinkItemRepository;
import com.both.testing_pilot_backend.service.PublicShareLinkItemService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class PublicShareLinkItemImpl implements PublicShareLinkItemService {
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;
}
