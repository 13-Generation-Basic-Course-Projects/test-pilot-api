package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.VariablesRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.model.Variables;
import com.both.testing_pilot_backend.service.VariablesService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/variables")
@Tag(name = "Variables", description = "Operations related to managing project variables")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VariablesController {
    private final VariablesService variablesService;
    private final AuthUtils authUtils;

    @Operation(
            summary = "Get all variables (for current user's projects)",
            description = "Fetches a list of all variables where the current user is an owner or collaborator of the parent project. Admin can see all.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved variables",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))) // ProblemDetail for errors
            }
    )
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<Variables>>> getAllVariables() {
        boolean isAdmin = authUtils.getUserDetails().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Variables> variables;
        if (isAdmin) {
            variables = variablesService.getAllVariables();
        } else {
            variables = variablesService.getAllVariables();
        }

        CustomApiResponse<List<Variables>> apiResponse = CustomApiResponse.<List<Variables>>builder()
                .message("Variables fetched successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .payload(variables)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

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
    public ResponseEntity<CustomApiResponse<Variables>> getVariableById(@PathVariable UUID id) {
        Variables variable = variablesService.getVariablesByVariableId(id); // Service should throw NotFoundException if null

        CustomApiResponse<Variables> apiResponse = CustomApiResponse.<Variables>builder()
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
                    @ApiResponse(responseCode = "200", description = "Variables fetched successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden: Not authorized to view variables for this project",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
//    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#projectId)")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<CustomApiResponse<List<Variables>>> getVariablesByProjectId(@PathVariable UUID projectId) {
        List<Variables> variables = variablesService.getVariablesByProjectId(projectId);

        CustomApiResponse<List<Variables>> apiResponse = CustomApiResponse.<List<Variables>>builder()
                .message("Variables fetched successfully for project.")
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
//    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#request.projectId)")
    @PostMapping
    public ResponseEntity<CustomApiResponse<Variables>> createVariable(@Valid @RequestBody VariablesRequest request) {
        Variables createdVariable = variablesService.saveVariable(request);

        CustomApiResponse<Variables> apiResponse = CustomApiResponse.<Variables>builder()
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
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Variables>> updateVariable(@PathVariable UUID id, @Valid @RequestBody VariablesRequest request) {
        Variables updatedVariable = variablesService.updateVariable(id, request);

        CustomApiResponse<Variables> apiResponse = CustomApiResponse.<Variables>builder()
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
//    @PreAuthorize("@projectSecurity.isProjectOwnerOrCollaborator(#projectId)")
    @PatchMapping("/project/{projectId}/enable")
    public ResponseEntity<CustomApiResponse<?>> updateEnabledStatus(@PathVariable UUID projectId,
                                                                    @RequestParam @Schema(description = "Set to true to enable, false to disable", example = "true") boolean isEnabled) {
        variablesService.changeEnabled(projectId, isEnabled);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Variables enabled status updated successfully for project " + projectId + ".")
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
        variablesService.deleteVariable(id);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Variable deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
