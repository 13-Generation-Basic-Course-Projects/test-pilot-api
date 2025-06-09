// src/main/java/com/both/testing_pilot_backend/controller/TestCaseController.java
package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.TestCaseRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.service.TestCaseService;
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
@Tag(name = "Test Case", description = "Operations related to managing test cases for projects and predefined types")
@RequiredArgsConstructor
@RequestMapping("/api/v1/test-cases")
@SecurityRequirement(name = "bearerAuth")
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping
    @Operation(
            summary = "Create a new test case",
            description = "Creates a new test case. If 'isPredefined' is true, 'projectId' must be null. If 'isPredefined' is false, 'projectId' must be provided.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Test case created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to create for this project/predefined type", content = @Content),
                    @ApiResponse(responseCode = "404", description = "DataType or Project not found", content = @Content)
            }
    )

    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.getProjectId())")
    public ResponseEntity<CustomApiResponse<TestCase>> createTestCase(@Valid @RequestBody TestCaseRequest request) {
        TestCase createdTestCase = testCaseService.createTestCase(request);
        CustomApiResponse<TestCase> apiResponse = CustomApiResponse.<TestCase>builder()
                .message("Test case created successfully.")
                .status(HttpStatus.CREATED)
                .success(true)
                .payload(createdTestCase)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/by-project/{projectId}")
    @Operation(
            summary = "Get test cases by Project ID",
            description = "Fetches all custom test cases belonging to a specific project. Restricted to project owners.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Test cases fetched successfully"),
                    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view test cases for this project", content = @Content)
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#projectId)")
    public ResponseEntity<CustomApiResponse<List<TestCase>>> getTestCasesByProjectId(@PathVariable UUID projectId) {
        List<TestCase> testCases = testCaseService.getTestCasesByProjectId(projectId);
        CustomApiResponse<List<TestCase>> apiResponse = CustomApiResponse.<List<TestCase>>builder()
                .message("Test cases for project fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(testCases)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/predefined")
    @Operation(
            summary = "Get all predefined test cases",
            description = "Fetches a list of all predefined test cases. Accessible to all authenticated users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved predefined test cases"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    // @PreAuthorize("isAuthenticated()") // Example: Any authenticated user can view predefined
    public ResponseEntity<CustomApiResponse<List<TestCase>>> getPredefinedTestCases() {
        List<TestCase> testCases = testCaseService.getPredefinedTestCases();
        CustomApiResponse<List<TestCase>> apiResponse = CustomApiResponse.<List<TestCase>>builder()
                .message("Predefined test cases fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(testCases)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing test case",
            description = "Updates an existing test case by its UUID. Access is restricted to project owners or admins for predefined.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Test case updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to update this test case", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Test case, DataType, or Project not found", content = @Content)
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.getProjectId())")
    public ResponseEntity<CustomApiResponse<TestCase>> updateTestCase(@PathVariable UUID id, @Valid @RequestBody TestCaseRequest request) {
        TestCase updatedTestCase = testCaseService.updateTestCase(id, request);
        CustomApiResponse<TestCase> apiResponse = CustomApiResponse.<TestCase>builder()
                .message("Test case updated successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .payload(updatedTestCase)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a test case",
            description = "Deletes a test case by its UUID. Access is restricted to project owners or admins for predefined.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Test case deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to delete this test case", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Test case not found", content = @Content)
            }
    )
    @PreAuthorize("(@testCaseSecurity.canModifyAndDeleteTestCase(#id))") // THIS IS THE KEY LINE
    public ResponseEntity<CustomApiResponse<?>> deleteTestCase(@PathVariable UUID id) {
        testCaseService.deleteTestCase(id);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Test case deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
