package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.jwt.JwtService;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.model.PublicShareLinkItem;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.repository.PublicShareLinkItemRepository;
import com.both.testing_pilot_backend.repository.PublicShareLinkRepository;
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.service.PublicShareLinkItemService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicShareLinkItemImpl implements PublicShareLinkItemService {
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final RequestRepository requestRepository;
    private final JwtService jwtService;

    @Override
    public List<PublicShareLinkItem> getAllPublicShareLinkItems() {
        List<PublicShareLinkItem> linkItems = publicShareLinkItemRepository.getAllPublicShareLinkItems();
        if (linkItems.isEmpty()) {
            throw new NotFoundException("No public share link items found.");
        }
        return publicShareLinkItemRepository.getAllPublicShareLinkItems();
    }

    @Override
    public PublicShareLinkItem getPublicShareLinkItemById(UUID id) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        PublicShareLinkItem linkItem = publicShareLinkItemRepository.getPublicShareLinkItemById(id);
        if (linkItem == null){
            throw new NotFoundException("Public share link not found with ID:" + id);
        }
        return linkItem;
    }

    @Override
    public PublicShareLinkItem createPublicShareLinkItem(PublicShareLinkItemRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        return publicShareLinkItemRepository.createPublicShareLinkItem(request);
    }

    @Override
    public PublicShareLinkItem updatePublicShareLinkItem(UUID id, PublicShareLinkItemRequest request) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        if (request == null) {
            throw new BadRequestException("Request body cannot be null.");
        }
        return publicShareLinkItemRepository.updatePublicShareLinkItem(id, request);
    }

    @Override
    public PublicShareLinkItem deletePublicShareLinkItemById(UUID id) {
        if (id == null) {
            throw new BadRequestException("Public share link ID cannot be null.");
        }
        PublicShareLinkItem deletedLinkItem = publicShareLinkItemRepository.deletePublicShareLinkItemById(id);
        if (deletedLinkItem == null){
            throw new NotFoundException("Public share link not found with ID:" + id);
        }
        return deletedLinkItem;
    }

    @Override
    public List<Request> getSharedContent(String token) {
        Claims claims;

        claims = jwtService.extractAllClaim(token);

        LocalDateTime jwtExpireAt = LocalDateTime.parse(claims.get("ea", String.class));
        System.out.println("jwtExpireAt" + jwtExpireAt);

        if (jwtExpireAt.isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Token has expired");
        }

        List<PublicShareLink> links = publicShareLinkRepository.findByToken(token);
        if (links == null){
            throw new NotFoundException("links cannot be found");
        }

        List<PublicShareLinkItem> allItems = new ArrayList<>();
        for (PublicShareLink link : links){
            List<PublicShareLinkItem> items = publicShareLinkItemRepository.findByShareLinkId(link.getShareLinkId());
            allItems.addAll(items);
        }

        List<Request> requests = new ArrayList<>();
        for (PublicShareLinkItem item : allItems) {
            Request request = requestRepository.findById(item.getItemId());
            if (request != null) {
                requests.add(request);
            }
        }
        return requests;
    }
}
