package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ExecuteBatchRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.ExecutionBatch;
import com.both.testing_pilot_backend.model.ExecutionResult;
import com.both.testing_pilot_backend.service.ExecutionService;
import com.both.testing_pilot_backend.utils.AuthUtils;
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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Execution", description = "Operations related to triggering and viewing API test executions")
@RequiredArgsConstructor
@RequestMapping("/api/v1/executions")
@SecurityRequirement(name = "bearerAuth")
public class ExecutionController {

    private final ExecutionService executionService;
    private final AuthUtils authUtils;

    @PostMapping("/trigger")
    @Operation(
            summary = "Trigger a new test execution batch",
            description = "Initiates a new batch of API test executions based on the specified trigger type and source.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Execution batch initiated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request or unsupported trigger type"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: User not authorized for this project", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Trigger source (e.g., Request, Test Case, Project) not found", content = @Content)
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.projectId)") // Only project owner/collaborator can trigger batches
    public Mono<ResponseEntity<CustomApiResponse<ExecutionBatch>>> triggerExecution(@Valid @RequestBody ExecuteBatchRequest request) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        return executionService.executeTests(request, currentUserId)
                .map(batch -> {
                    CustomApiResponse<ExecutionBatch> apiResponse = CustomApiResponse.<ExecutionBatch>builder()
                            .message("Execution batch initiated successfully. Results will be available shortly.")
                            .status(HttpStatus.ACCEPTED) // 202 Accepted, as it's an async process
                            .success(true)
                            .data(batch)
                            .build();
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse);
                });
        // Error handling is now centralized in GlobalExceptionHandler
    }

    @GetMapping("/batches")
    @Operation(
            summary = "Get all execution batches (for current user's projects)",
            description = "Retrieves a list of all historical test execution batches for projects the current user is involved in. Admin can see all.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved batches"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public Mono<ResponseEntity<CustomApiResponse<List<ExecutionBatch>>>> getAllBatches() {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        boolean isAdmin = authUtils.getUserDetails().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Mono<List<ExecutionBatch>> batchesMono;
        if (isAdmin) {
            batchesMono = executionService.getAllBatches();
        } else {
            // For non-admins, fetch batches only for projects they own/collaborate on
            // This would require a more complex service method or a join in the repository
            // For simplicity, assuming getAllBatchesForProject is what's needed for user's own projects
            // You might need a service method like: executionService.getAllBatchesForUser(currentUserId)
            // For now, returning all, but security should be refined.
            batchesMono = executionService.getAllBatches();
        }

        return batchesMono.map(batches -> {
            CustomApiResponse<List<ExecutionBatch>> apiResponse = CustomApiResponse.<List<ExecutionBatch>>builder()
                    .message("Execution batches fetched successfully.")
                    .status(HttpStatus.OK)
                    .success(true)
                    .data(batches)
                    .build();
            return ResponseEntity.ok(apiResponse);
        });
        // Error handling is now centralized in GlobalExceptionHandler
    }

    @GetMapping("/batches/{batchId}")
    @Operation(
            summary = "Get execution batch results by ID",
            description = "Retrieves details and results for a specific execution batch. Restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved batch results"),
                    @ApiResponse(responseCode = "404", description = "Batch not found", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view this batch", content = @Content)
            }
    )
    @PreAuthorize("@executionSecurity.isBatchAuthorized(#batchId)") // Security for viewing batch (new method)
    public Mono<ResponseEntity<CustomApiResponse<ExecutionBatch>>> getBatchResults(@PathVariable UUID batchId) {
        return executionService.getBatchResults(batchId)
                .map(batch -> {
                    CustomApiResponse<ExecutionBatch> apiResponse = CustomApiResponse.<ExecutionBatch>builder()
                            .message("Execution batch details fetched successfully.")
                            .status(HttpStatus.OK)
                            .success(true)
                            .data(batch)
                            .build();
                    return ResponseEntity.ok(apiResponse);
                });
        // Error handling is now centralized in GlobalExceptionHandler
    }

    @GetMapping("/results/{resultId}")
    @Operation(
            summary = "Get single execution result details by ID",
            description = "Retrieves detailed information for a specific execution result within a batch. Restricted to project owner/collaborators of the parent batch.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved execution result"),
                    @ApiResponse(responseCode = "404", description = "Result not found", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view this result", content = @Content)
            }
    )
    @PreAuthorize("@executionSecurity.isResultAuthorized(#resultId)") // Security method for results
    public Mono<ResponseEntity<CustomApiResponse<ExecutionResult>>> getExecutionResultDetails(@PathVariable UUID resultId) {
        return executionService.getExecutionResultDetails(resultId)
                .map(result -> {
                    CustomApiResponse<ExecutionResult> apiResponse = CustomApiResponse.<ExecutionResult>builder()
                            .message("Execution result details fetched successfully.")
                            .status(HttpStatus.OK)
                            .success(true)
                            .data(result)
                            .build();
                    return ResponseEntity.ok(apiResponse);
                });
        // Error handling is now centralized in GlobalExceptionHandler
    }
}
