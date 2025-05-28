package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.service.PublicShareLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
//@SecurityRequirement(name = "bearerAuth")
public class PublicShareLinkController {
    private final PublicShareLinkService publicShareLinkService;
    @GetMapping
//    @Operation(
//        security = @SecurityRequirement(name = "bearerAuth"),
//        summary = "Retrieve list of public share link",
//        description = "Fetches public share links.",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved public share links"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//        }
//    )
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

    @GetMapping("/{public-share-link-id}")
//    @Operation(
//        security = @SecurityRequirement(name = "bearerAuth"),
//        summary = "Retrieve a public share link by ID",
//        description = "Fetch a single public share link by UUID.",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved a single public share link"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//            @ApiResponse(responseCode = "404", description = "public share link not found", content = @Content)
//        }
//    )
    public ResponseEntity<CustomApiResponse<PublicShareLink>> getPublicShareLinkById(@PathVariable("public-share-link-id") UUID id) {
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
//    @Operation(
//        security = @SecurityRequirement(name = "bearerAuth"),
//        summary = "Create a new public share link",
//        description = "Post a single public share link by PublicShareLinkRequest.",
//        responses = {
//            @ApiResponse(responseCode = "201", description = "Public share link created successfully"),
//            @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
//        }
//    )
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

    @PutMapping("/{public-share-link-id}")
//    @Operation(
//        security = @SecurityRequirement(name = "bearerAuth"),
//        summary = "Update an existing public share link by their ID",
//        description = "Update a public share link by PublicShareLinkRequest and UUID.",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "Public share link updated successfully"),
//            @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//            @ApiResponse(responseCode = "404", description = "Public share link not found", content = @Content)
//        }
//    )
    public ResponseEntity<CustomApiResponse<PublicShareLink>> updatePublicShareLink(@PathVariable("public-share-link-id") UUID id,
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

    @DeleteMapping("/{public-share-link-id}")
//    @Operation(
//        security = @SecurityRequirement(name = "bearerAuth"),
//        summary = "Delete a public share link by ID",
//        description = "Delete a single public share link by UUID.",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "Successfully delete a public share link"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//            @ApiResponse(responseCode = "404", description = "public share link not found", content = @Content)
//        }
//    )
    public ResponseEntity<CustomApiResponse<PublicShareLink>> deletePublicShareLink(@PathVariable("public-share-link-id") UUID id) {
        publicShareLinkService.deletePublicShareLink(id);

        CustomApiResponse<PublicShareLink> apiResponse = CustomApiResponse.<PublicShareLink>builder()
            .message("Public Share Link has been deleted successfully")
            .status(HttpStatus.NO_CONTENT)
            .success(true)
            .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
