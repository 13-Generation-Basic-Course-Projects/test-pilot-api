// src/main/java/com/both/testing_pilot_backend/controller/RequestTestCaseController.java
package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.RequestTestCaseRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.security.expression.RequestTestCaseSecurity;
import com.both.testing_pilot_backend.service.RequestTestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "RequestTestCase", description = "Operations for managing links between Requests and Test Cases")
@RequiredArgsConstructor
@RequestMapping("/api/v1/request-test-cases")
@SecurityRequirement(name = "bearerAuth")
public class RequestTestCaseController {

    private final RequestTestCaseService requestTestCaseService;
    private final RequestTestCaseSecurity requestTestCaseSecurity; // Inject security bean

    @PostMapping
    @Operation(
            summary = "Create a new Request-Test Case link",
            description = "Links a specific Test Case to a Request with application context and expected success status. Requires project ownership/collaboration for the Request's project.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Link created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors (e.g., Request/TestCase not found, duplicate link)", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to create links for this project", content = @Content)
            }
    )
    @PreAuthorize("@requestTestCaseSecurity.canCreateLinkForRequest(#request.requestId)") // Security check for creation
    public ResponseEntity<CustomApiResponse<RequestTestCase>> createRequestTestCase(@Valid @RequestBody RequestTestCaseRequest request) {
        RequestTestCase createdLink = requestTestCaseService.createRequestTestCase(request);
        CustomApiResponse<RequestTestCase> apiResponse = CustomApiResponse.<RequestTestCase>builder()
                .message("Request-Test Case link created successfully.")
                .status(HttpStatus.CREATED)
                .success(true)
                .data(createdLink)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a Request-Test Case link by ID",
            description = "Fetches a specific link by its UUID. Access is restricted to project owners/collaborators of the linked Request's project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Link fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view this link", content = @Content)
            }
    )
    @PreAuthorize("@requestTestCaseSecurity.isAuthorized(#id)") // Security check for viewing
    public ResponseEntity<CustomApiResponse<RequestTestCase>> getRequestTestCaseById(@PathVariable UUID id) {
        RequestTestCase link = requestTestCaseService.getRequestTestCaseById(id);
        CustomApiResponse<RequestTestCase> apiResponse = CustomApiResponse.<RequestTestCase>builder()
                .message("Request-Test Case link fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .data(link)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/by-request/{requestId}")
    @Operation(
            summary = "Get all Request-Test Case links for a specific Request",
            description = "Fetches all links associated with a given Request. Access is restricted to project owners/collaborators of the Request's project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Links fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Request not found", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view links for this request", content = @Content)
            }
    )
    @PreAuthorize("@requestSecurity.isRequestOwnerOrCollaborator(#requestId)") // Re-use RequestSecurity for Request's access
    public ResponseEntity<CustomApiResponse<List<RequestTestCase>>> getRequestTestCasesByRequestId(@PathVariable UUID requestId) {
        List<RequestTestCase> links = requestTestCaseService.getRequestTestCasesByRequestId(requestId);
        CustomApiResponse<List<RequestTestCase>> apiResponse = CustomApiResponse.<List<RequestTestCase>>builder()
                .message("Request-Test Case links fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .data(links)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing Request-Test Case link",
            description = "Updates the properties (application context, expected success) of an existing link. Request and Test Case IDs are immutable.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Link updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to update this link", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content)
            }
    )
    @PreAuthorize("@requestTestCaseSecurity.isAuthorized(#id)") // Security check for update
    public ResponseEntity<CustomApiResponse<RequestTestCase>> updateRequestTestCase(@PathVariable UUID id, @Valid @RequestBody RequestTestCaseRequest request) {
        RequestTestCase updatedLink = requestTestCaseService.updateRequestTestCase(id, request);
        CustomApiResponse<RequestTestCase> apiResponse = CustomApiResponse.<RequestTestCase>builder()
                .message("Request-Test Case link updated successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .data(updatedLink)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Request-Test Case link",
            description = "Deletes a specific link by its UUID. Access is restricted to project owners/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Link deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to delete this link", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Link not found", content = @Content)
            }
    )
    @PreAuthorize("@requestTestCaseSecurity.isAuthorized(#id)") // Security check for deletion
    public ResponseEntity<CustomApiResponse<?>> deleteRequestTestCase(@PathVariable UUID id) {
        requestTestCaseService.deleteRequestTestCase(id);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Request-Test Case link deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
