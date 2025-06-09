package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.VariableRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.Variable;
import com.both.testing_pilot_backend.service.VariableService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/variables")
@Tag(name = "Variable", description = "Operations related to managing project variables")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VariableController {
    private final VariableService variableService;
    private final AuthUtils authUtils;

//    @Operation(
//            summary = "Get all variables (for current user's projects)",
//            description = "Fetches a list of all variables where the current user is an owner or collaborator of the parent project. Admin can see all.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successfully retrieved variables",
//                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized",
//                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))) // ProblemDetail for errors
//            }
//    )
//    @GetMapping
//    public ResponseEntity<CustomApiResponse<List<Variable>>> getAllVariables() {
//        boolean isAdmin = authUtils.getUserDetails().getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//
//        List<Variable> variables;
//        if (isAdmin) {
//            variables = variableService.getAllVariables();
//        } else {
//            variables = variableService.getAllVariables();
//        }
//
//        CustomApiResponse<List<Variable>> apiResponse = CustomApiResponse.<List<Variable>>builder()
//                .message("Variable fetched successfully.")
//                .status(HttpStatus.OK)
//                .success(true)
//                .timestamps(LocalDateTime.now())
//                .payload(variables)
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }

    @Operation(
            summary = "Get variable by ID",
            description = "Fetches a single variable by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variable fetched successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Variable not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view this variable",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Variable>> getVariableById(@PathVariable UUID id) {
        Variable variable = variableService.getVariablesByVariableId(id); // Service should throw NotFoundException if null

        CustomApiResponse<Variable> apiResponse = CustomApiResponse.<Variable>builder()
                .message("Variable fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(variable)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Get variables by project ID",
            description = "Fetches all variables belonging to a specific project. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variable fetched successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view variables for this project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/project/{projectId}")
    public ResponseEntity<CustomApiResponse<List<Variable>>> getVariablesByProjectId(@PathVariable UUID projectId) {
        List<Variable> variables = variableService.getVariablesByProjectId(projectId);

        CustomApiResponse<List<Variable>> apiResponse = CustomApiResponse.<List<Variable>>builder()
                .message("Variable fetched successfully for project.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(variables)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Create a new variable",
            description = "Creates a new variable within a specified project. Requires project ownership/collaboration.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Variable created successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to create in this project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: Variable with this name already exists in project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.projectId)")
    @PostMapping
    public ResponseEntity<CustomApiResponse<Variable>> createVariable(@Valid @RequestBody VariableRequest request) {
        Variable createdVariable = variableService.saveVariable(request);

        CustomApiResponse<Variable> apiResponse = CustomApiResponse.<Variable>builder()
                .message("Variable created successfully.")
                .status(HttpStatus.CREATED)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(createdVariable)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(
            summary = "Update a variable",
            description = "Updates an existing variable by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variable updated successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to update this variable",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Variable or new Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: Variable with this name already exists in target project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.projectId)")
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Variable>> updateVariable(@PathVariable UUID id, @Valid @RequestBody VariableRequest request) {
        Variable updatedVariable = variableService.updateVariable(id, request);

        CustomApiResponse<Variable> apiResponse = CustomApiResponse.<Variable>builder()
                .message("Variable updated successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(updatedVariable)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Enable or disable variables for a project",
            description = "Changes the enabled status for all variables within a specific project. Requires project ownership/collaboration.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variable enabled status updated successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to modify variables in this project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#projectId)")
    @PatchMapping("/project/{projectId}/enable")
    public ResponseEntity<CustomApiResponse<?>> updateEnabledStatus(@PathVariable UUID projectId,
                                                                    @RequestParam @Schema(description = "Set to true to enable, false to disable", example = "true") boolean isEnabled) {
        variableService.changeEnabled(projectId, isEnabled);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Variable enabled status updated successfully for project " + projectId + ".")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Delete a variable (soft delete)",
            description = "Soft deletes a variable by its UUID. Access restricted to project owner/collaborators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variable deleted successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to delete this variable",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Variable not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<?>> deleteVariable(@PathVariable UUID id) {
        variableService.deleteVariable(id);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Variable deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
