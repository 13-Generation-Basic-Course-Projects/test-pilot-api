package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.CollectionRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.Collection;
import com.both.testing_pilot_backend.security.expression.CollectionSecurity;
import com.both.testing_pilot_backend.service.CollectionService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collections")
@Tag(name = "Collection", description = "Operations related to managing collections of API requests")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CollectionsController {

    private final CollectionService collectionService;
    private final CollectionSecurity collectionSecurity;
    private final AuthUtils authUtils;

    @GetMapping("/by-project/{project-id}")
    @Operation(
            summary = "Retrieve all collections (for current user's projects)",
            description = "Fetches a list of all collections where the current user is an owner or collaborator of the parent project. Admin can see all.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved collections",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    public ResponseEntity<CustomApiResponse<List<Collection>>> getCollectionByProjectId(@PathVariable("project-id") UUID projectId) {
         List<Collection> collections = collectionService.getCollectionsByProjectId(projectId);
        CustomApiResponse<List<Collection>> apiResponse = CustomApiResponse.<List<Collection>>builder()
                .message("Collections fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(collections)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get collection by ID",
            description = "Fetches a single collection by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collection fetched successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Collection not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view this collection",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@collectionSecurity.isCollectionOwnerOrCollaborator(#id)")
    public ResponseEntity<CustomApiResponse<Collection>> getById(@PathVariable("id") UUID id) { // Renamed
        Collection collection = collectionService.getCollectionById(id);
        CustomApiResponse<Collection> apiResponse = CustomApiResponse.<Collection>builder()
                .message("Collection fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(collection)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    @Operation(
            summary = "Create a new collection",
            description = "Creates a new collection within a specified project. Requires project ownership/collaboration.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Collection created successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to create in this project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: Collection with this name already exists in project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@collectionSecurity.canCreateCollectionInProject(#request.projectId)")
    public ResponseEntity<CustomApiResponse<Collection>> create(@Valid @RequestBody CollectionRequest request) throws AccessDeniedException {
        Collection createdCollection = collectionService.createCollection(request);
        CustomApiResponse<Collection> apiResponse = CustomApiResponse.<Collection>builder()
                .message("Collection created successfully.")
                .status(HttpStatus.CREATED)
                .success(true)
                .payload(createdCollection)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing collection",
            description = "Updates an existing collection by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collection updated successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to update this collection",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Collection or new Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: Collection with this name already exists in target project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@collectionSecurity.isCollectionOwnerOrCollaborator(#id)")
    public ResponseEntity<CustomApiResponse<Collection>> update(@PathVariable UUID id, @Valid @RequestBody CollectionRequest request) throws AccessDeniedException { // Renamed
        Collection updatedCollection = collectionService.updateCollection(id, request);
        CustomApiResponse<Collection> apiResponse = CustomApiResponse.<Collection>builder()
                .message("Collection updated successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(updatedCollection)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a collection (soft delete)",
            description = "Soft deletes a collection by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collection deleted successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to delete this collection",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Collection not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@collectionSecurity.isCollectionOwnerOrCollaborator(#id)")
    public ResponseEntity<CustomApiResponse<?>> delete(@PathVariable UUID id) throws AccessDeniedException {
        collectionService.deleteCollection(id);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Collection deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
