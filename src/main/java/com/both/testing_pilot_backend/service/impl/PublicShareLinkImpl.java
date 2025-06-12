package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.*;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShareLinkImpl implements PublicShareLinkService {
    private final PublicShareLinkRepository publicShareLinkRepository;

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

}
