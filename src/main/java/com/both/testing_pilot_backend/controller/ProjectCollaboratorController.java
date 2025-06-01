package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse; // Import CustomApiResponse
import com.both.testing_pilot_backend.model.ProjectCollaborator;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import io.swagger.v3.oas.annotations.Operation; // Import Operation for Swagger
import io.swagger.v3.oas.annotations.media.Content; // Import Content for Swagger
import io.swagger.v3.oas.annotations.media.Schema; // Import Schema for Swagger
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Import ApiResponse for Swagger
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag; // Import Tag for Swagger
import jakarta.validation.Valid; // Import Valid for DTO validation
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collaborators") // Changed API path for consistency
@Tag(name = "Project Collaborator", description = "Operations related to inviting and managing project collaborators") // Added Tag
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProjectCollaboratorController {
    private final ProjectCollaboratorService projectCollaboratorService;

    @PostMapping("/invite-link")
    @Operation(
            summary = "Invite a new collaborator to a project",
            description = "Sends an invitation email to a user to become a collaborator on a project. Only the project owner can invite.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitation sent successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., user not found, self-invite)",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))), // ProblemDetail for errors
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (not project owner)",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Project or invited user not found",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict (user already collaborator/owner)",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    public ResponseEntity<CustomApiResponse<ProjectCollaborator>> inviteLink(@Valid @RequestBody ProjectCollaboratorRequest request) {
        ProjectCollaborator projectCollaborator = projectCollaboratorService.inviteCollaborator(request);

        CustomApiResponse<ProjectCollaborator> apiResponse  = CustomApiResponse.<ProjectCollaborator>builder()
                .message("Invitation was sent successfully to user email " + request.getEmail())
                .status(HttpStatus.OK)
                .success(true)
                .data(projectCollaborator)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("accept/invite/{project-collaboration-id}")
    public ResponseEntity<CustomApiResponse<ProjectCollaborator>> acceptInviteLink(@PathVariable("project-collaboration-id") UUID id){
        ProjectCollaborator projectCollaborator = projectCollaboratorService.acceptInviteLink(id);

        CustomApiResponse<ProjectCollaborator> apiResponse = CustomApiResponse.<ProjectCollaborator>builder()
                .message("Successfully accepted the link invited")
                .status(HttpStatus.OK)
                .success(true)
                .data(projectCollaborator)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
