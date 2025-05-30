package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.exceptions.BadRequestException;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.*;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PublicShareLinkImpl implements PublicShareLinkService {
    private final AuthUtils authUtils;
    private final PublicShareLinkRepository publicShareLinkRepository;
    private final PublicShareLinkItemRepository publicShareLinkItemRepository;
    private final CollectionRepository collectionRepository;
    private final RequestRepository requestRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<Request> getSharedContent(String token) {
        if (token == null){
            throw new BadRequestException("Token cannot be null");
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


    @Override
    public String createShareLink(UUID projectId) {
        if (projectId == null) {
            throw new BadRequestException("Project ID cannot be null.");
        }
        String token = String.valueOf(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(7);

        Project existProject = projectRepository.findByProjectId(projectId);
        if (existProject == null){
            throw new NotFoundException("Project id cannot be found.");
        }

        List<Collection> allCollections = collectionRepository.findByProjectId(existProject.getProjectId());

        for (Collection collection : allCollections){
            Collection existCollection =  collectionRepository.findById(collection.getId());
            List<Request> requests = requestRepository.findByCollectionId(existCollection.getId());

            PublicShareLinkRequest link = new PublicShareLinkRequest();
            link.setToken(token);
            link.setSharedItemType(existCollection.getName());
            link.setSharedItemId(existCollection.getId());
            link.setExpireAt(expireAt);

            PublicShareLink shareLink = publicShareLinkRepository.createPublicShareLink(link, authUtils.getUserDetails().getUserId());

            List<PublicShareLinkItemRequest> items = requests.stream()
                .map(req -> {
                    PublicShareLinkItemRequest item = new PublicShareLinkItemRequest();
                    item.setItemType(req.getName());
                    item.setItemId(req.getId());
                    item.setShareLinkId(shareLink.getShareLinkId());
                    publicShareLinkItemRepository.createPublicShareLinkItem(item);
                    return item;
                })
                .toList();
        }
        return token;
    }



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
