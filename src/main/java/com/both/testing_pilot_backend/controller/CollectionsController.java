package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.CollectionsRequest;
import com.both.testing_pilot_backend.model.Collections;
import com.both.testing_pilot_backend.service.CollectionsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/collections")
@SecurityRequirement(name = "bearerAuth") // Enables Bearer token usage in Swagger
public class CollectionsController {

    private final CollectionsService collectionsService;

    public CollectionsController(CollectionsService collectionsService) {
        this.collectionsService = collectionsService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Collections> getAllCollections() {
        return collectionsService.getAllCollections();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Collections getById(@PathVariable("id") UUID id) {
        return collectionsService.getCollectionsById(id);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Collections create(@RequestBody CollectionsRequest request) {
        return collectionsService.saveCollections(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Collections update(@PathVariable UUID id, @RequestBody CollectionsRequest request) {
        Collections updated = new Collections();
        updated.setCollectionsId(id);
        updated.setCollectionsName(request.getCollectionsName());
        updated.setProjectId(request.getProjectId());
        return collectionsService.updateCollections(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public UUID delete(@PathVariable UUID id) {
        return collectionsService.deleteCollections(id);
    }
}
