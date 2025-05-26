package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.RequestRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.service.RequestService;
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

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Request", description = "Simple CRUD operations for requests")
@RequiredArgsConstructor
@RequestMapping("/api/v1/requests")
@SecurityRequirement(name = "bearerAuth")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @Operation(
            summary = "Create a new request",
            description = "Creates a new request associated with a specific collection.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Request created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<Request>> createRequest(@Valid @RequestBody RequestRequest request) {
        Request createdRequest = requestService.createRequest(request);
        CustomApiResponse<Request> apiResponse = CustomApiResponse.<Request>builder()
                .message("Request created successfully")
                .status(HttpStatus.CREATED)
                .success(true)
                .data(createdRequest)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all requests",
            description = "Fetches a list of all requests.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved requests")
            }
    )
    public ResponseEntity<CustomApiResponse<List<Request>>> getAllRequests() {
        List<Request> requests = requestService.getAllRequests();
        CustomApiResponse<List<Request>> apiResponse = CustomApiResponse.<List<Request>>builder()
                .message("Requests have been fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(requests)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{requestId}")
    @Operation(
            summary = "Get request by ID",
            description = "Fetches a single request by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Request not found", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<Request>> getRequestById(@PathVariable UUID requestId) {
        Request request = requestService.getRequestById(requestId);
        CustomApiResponse<Request> apiResponse = CustomApiResponse.<Request>builder()
                .message("Request fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(request)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{requestId}")
    @Operation(
            summary = "Update an existing request",
            description = "Updates an existing request identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Request not found", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<Request>> updateRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody RequestRequest requestDto) {
        Request updatedRequest = requestService.updateRequest(requestId, requestDto);
        CustomApiResponse<Request> apiResponse = CustomApiResponse.<Request>builder()
                .message("Request updated successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(updatedRequest)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{requestId}")
    @Operation(
            summary = "Delete a request",
            description = "Deletes a request identified by its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Request not found", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<?>> deleteRequest(@PathVariable UUID requestId) {
        requestService.deleteRequest(requestId);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Request deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/by-collection/{collectionId}")
    @Operation(
            summary = "Get requests by Collection ID",
            description = "Fetches all requests belonging to a specific Collection.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Requests fetched successfully")
            }
    )
    public ResponseEntity<CustomApiResponse<List<Request>>> getRequestsByCollectionId(@PathVariable UUID collectionId) {
        List<Request> requests = requestService.getRequestsByCollectionId(collectionId);
        CustomApiResponse<List<Request>> apiResponse = CustomApiResponse.<List<Request>>builder()
                .message("Requests by collection fetched successfully")
                .status(HttpStatus.OK)
                .success(true)
                .data(requests)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
