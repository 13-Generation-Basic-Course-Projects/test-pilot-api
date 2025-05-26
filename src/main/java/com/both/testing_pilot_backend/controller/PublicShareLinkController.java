package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Public share link", description = "Operations related to public share link creation")
@RequiredArgsConstructor
@RequestMapping("/api/v1/public-share-link")
@SecurityRequirement(name = "bearerAuth")
public class PublicShareLinkController {

    private final PublicShareLinkService publicShareLinkService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<PublicShareLink>>> getAllPublicShareLinks() {
        List<PublicShareLink> publicShareLinks = publicShareLinkService.getAllPublicShareLinks();

        CustomApiResponse<List<PublicShareLink>> apiResponse = CustomApiResponse.<List<PublicShareLink>>builder()
                .message("Public Share Links have been fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(publicShareLinks)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PublicShareLink>> getPublicShareLinkById(@PathVariable UUID id) {
        PublicShareLink publicShareLink = publicShareLinkService.getPublicShareLinkById(id);

        CustomApiResponse<PublicShareLink> apiResponse = CustomApiResponse.<PublicShareLink>builder()
                .message("Public Share Link has been fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(publicShareLink)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<PublicShareLink>> createPublicShareLink(@RequestBody PublicShareLinkRequest request) {
        PublicShareLink publicShareLink = publicShareLinkService.createPublicShareLink(request);

        CustomApiResponse<PublicShareLink> apiResponse = CustomApiResponse.<PublicShareLink>builder()
                .message("Public Share Link has been created successfully")
                .status(HttpStatus.CREATED)
                .success(true)
                .data(publicShareLink)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PublicShareLink>> updatePublicShareLink(@PathVariable UUID id,
                                                                                    @RequestBody PublicShareLinkRequest request) {
        PublicShareLink updatedLink = publicShareLinkService.updatePublicShareLink(id, request);

        CustomApiResponse<PublicShareLink> apiResponse = CustomApiResponse.<PublicShareLink>builder()
                .message("Public Share Link has been updated successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(updatedLink)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<PublicShareLink>> deletePublicShareLink(@PathVariable UUID id) {
        publicShareLinkService.deletePublicShareLink(id);

        CustomApiResponse<PublicShareLink> apiResponse = CustomApiResponse.<PublicShareLink>builder()
                .message("Public Share Link has been deleted successfully")
                .status(HttpStatus.NO_CONTENT)
                .success(true)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
