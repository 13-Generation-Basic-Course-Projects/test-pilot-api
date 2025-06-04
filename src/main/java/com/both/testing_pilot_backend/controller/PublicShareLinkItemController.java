package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.PublicShareLinkItem;
import com.both.testing_pilot_backend.service.PublicShareLinkItemService;
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
@Tag(name = "Public share link item", description = "Operations related to public share link item creation")
@RequiredArgsConstructor
@RequestMapping("/api/v1/public-share-link-item")
@SecurityRequirement(name = "bearerAuth")
public class PublicShareLinkItemController {
    private final PublicShareLinkItemService publicShareLinkItemService;

    @GetMapping
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Retrieve list of public share link items",
        description = "Fetches public share link items",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved public share link items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<List<PublicShareLinkItem>>> getAllPublicShareLinkItems() {
        List<PublicShareLinkItem> publicShareLinkItems = publicShareLinkItemService.getAllPublicShareLinkItems();

        CustomApiResponse<List<PublicShareLinkItem>> apiResponse = CustomApiResponse.<List<PublicShareLinkItem>>builder()
            .message("Public Share Link items have been fetched successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(publicShareLinkItems)
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{share-link-item-id}")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Retrieve a single of public share link item",
        description = "Fetches a single public share link item",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a single public share link item"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Share link item not found", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<PublicShareLinkItem>> getPublicShareLinkItemById(@PathVariable("share-link-item-id") UUID id){
        PublicShareLinkItem publicShareLinkItem = publicShareLinkItemService.getPublicShareLinkItemById(id);

        CustomApiResponse<PublicShareLinkItem> apiResponse = CustomApiResponse.<PublicShareLinkItem>builder()
            .message("Public Share Link item have been fetched successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(publicShareLinkItem)
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Create a single of public share link item",
        description = "Post a single public share link item",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created a public share link item"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation errors in request body")
        }
    )
    public ResponseEntity<CustomApiResponse<PublicShareLinkItem>> createPublicShareLinkItem(@Valid @RequestBody PublicShareLinkItemRequest request){
        PublicShareLinkItem publicShareLinkItem = publicShareLinkItemService.createPublicShareLinkItem(request);

        CustomApiResponse<PublicShareLinkItem> apiResponse = CustomApiResponse.<PublicShareLinkItem>builder()
            .message("Public Share Link item have been created successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(publicShareLinkItem)
            .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{share-link-item-id}")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Create a single of public share link item",
        description = "Post a single public share link item",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created a public share link item"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation errors in request body"),
            @ApiResponse(responseCode = "404", description = "Share link item not found", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<PublicShareLinkItem>> updatePublicShareLinkItem(@PathVariable("share-link-item-id") UUID id,@Valid @RequestBody PublicShareLinkItemRequest request){
        PublicShareLinkItem publicShareLinkItem = publicShareLinkItemService.updatePublicShareLinkItem(id, request);

        CustomApiResponse<PublicShareLinkItem> apiResponse = CustomApiResponse.<PublicShareLinkItem>builder()
            .message("Public Share Link item have been updated successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(publicShareLinkItem)
            .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{share-link-item-id}")
    @Operation(
        security = @SecurityRequirement(name = "bearerAuth"),
        summary = "Delete a single of public share link item",
        description = "Delete a single public share link item",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted a single public share link item"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Share link item not found", content = @Content)
        }
    )
    public ResponseEntity<CustomApiResponse<PublicShareLinkItem>> deletePublicShareLinkItemById(@PathVariable("share-link-item-id") UUID id){
        PublicShareLinkItem publicShareLinkItem = publicShareLinkItemService.deletePublicShareLinkItemById(id);

        CustomApiResponse<PublicShareLinkItem> apiResponse = CustomApiResponse.<PublicShareLinkItem>builder()
            .message("Public Share Link item have been deleted successfully")
            .status(HttpStatus.OK)
            .success(true)
            .timestamps(LocalDateTime.now())
            .payload(publicShareLinkItem)
            .build();
        return ResponseEntity.ok(apiResponse);
    }
}
