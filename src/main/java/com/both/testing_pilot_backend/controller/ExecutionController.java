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
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.projectId)")
    public Mono<ResponseEntity<CustomApiResponse<ExecutionBatch>>> triggerExecution(@Valid @RequestBody ExecuteBatchRequest request) {
        UUID currentUserId = authUtils.getUserDetails().getUserId();
        System.out.println("working in herer " + request.toString());
        return executionService.executeTests(request, currentUserId)
                .map(batch -> {
                    CustomApiResponse<ExecutionBatch> apiResponse = CustomApiResponse.<ExecutionBatch>builder()
                            .message("Execution batch initiated successfully. Results will be available shortly.")
                            .status(HttpStatus.ACCEPTED)
                            .success(true)
                            .payload(batch)
                            .build();
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponse);
                });
    }

    @GetMapping("/projects/batches/by-project/{project-id}")
    @Operation(
            summary = "Get all execution batches (for current user's projects)",
            description = "Retrieves a list of all historical test execution batches for projects the current user is involved in. Admin can see all.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved batches"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public ResponseEntity<CustomApiResponse<List<ExecutionBatch>>> getAllBatches(@PathVariable("project-id") UUID projectId) {


        List<ExecutionBatch> batch  = executionService.getAllBatchesForProject(projectId);
        System.out.println("The response is working in here");
        CustomApiResponse<List<ExecutionBatch>> apiResponse = CustomApiResponse.<List<ExecutionBatch>>builder()
                .message("Execution batches fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(batch)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
