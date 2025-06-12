package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.PublicShareLink;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Public share link", description = "Operations related to public share link creation")
@RequiredArgsConstructor
@RequestMapping("/api/v1/public-share-link")
@SecurityRequirement(name = "bearerAuth")
public class PublicShareLinkController {
    private final PublicShareLinkItemService publicShareLinkItemService;
    private final PublicShareLinkService publicShareLinkService;
    private final CollectionService collectionService;
    private final ProjectService projectService;
    private final RequestService requestService;

    @GetMapping("/verify-token/{token}")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Retrieve related payload endpoints, collections",
        description = "Fetches endpoints by token access",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved related payload endpoints, collections"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<List<Request>>> getSharedContent(@PathVariable String token) {
        List<Request> sharedLink = publicShareLinkItemService.getSharedContent(token);

        CustomApiResponse<List<Request>> apiResponse = CustomApiResponse.<List<Request>>builder()
            .message("Retrieve shared link related endpoints successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(sharedLink)
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/by-project/{project-id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create a token and take it in a single public share link",
            description = "Post a token and take it in a single public share link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created a token and take it in a public share link"),
                    @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<String>> shareLinkByProjectId(@PathVariable("project-id") UUID projectId) {
        String link = projectService.shareLinkByProjectId(projectId);

        CustomApiResponse<String> apiResponse = CustomApiResponse.<String>builder()
            .message("Share link created successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(link)
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/by-collection/{collection-id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create a token and take it in a single public share link",
            description = "Post a token and take it in a single public share link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created a token and take it in a public share link"),
                    @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<String>> shareLinkByCollectionId(@PathVariable("collection-id") List<UUID> collectionIds) {
        String link = collectionService.shareLinkByCollectionId(collectionIds);

        CustomApiResponse<String> apiResponse = CustomApiResponse.<String>builder()
                .message("Share link created successfully")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(link)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/by-request/{request-id}")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Create a token and take it in a single public share link",
            description = "Post a token and take it in a single public share link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created a token and take it in a public share link"),
                    @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<String>> shareLinkByRequestId(@PathVariable("request-id") List<UUID> requestIds) {
        String link = requestService.shareLinkByRequestId(requestIds);

        CustomApiResponse<String> apiResponse = CustomApiResponse.<String>builder()
                .message("Share link created successfully")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(link)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
