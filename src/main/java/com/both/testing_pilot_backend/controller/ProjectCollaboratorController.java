package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project-collaborator")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor

public class ProjectCollaboratorController {
    private final ProjectCollaboratorService projectCollaboratorService;
    @PostMapping("/invite")
    @Operation(
            summary = "Invite a user to a project by userId",
            description = "Adds a collaborator to a project by user UUID",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Collaborator invited successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Project or user not found")
            }
    )
    public ResponseEntity<CustomApiResponse<UUID>> inviteCollaborator(
            @Valid @RequestBody ProjectCollaboratorRequest request) {

        UUID collaboratorId = projectCollaboratorService.inviteUserToProject(
                request.getProjectId(),
                request.getUserId()
        );

        CustomApiResponse<UUID> response = CustomApiResponse.<UUID>builder()
                .message("Collaborator invited successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .data(collaboratorId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    @Operation(
            summary = "Check if a user is a collaborator on a project",
            description = "Returns true if the user is a collaborator of the given project, otherwise false.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Check successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid projectId or userId")
            }
    )
    public ResponseEntity<CustomApiResponse<Boolean>> isCollaborator(
            @RequestParam UUID projectId,
            @RequestParam UUID userId
    ) {
        boolean isCollaborator = projectCollaboratorService.isProjectCollaborator(projectId, userId);

        CustomApiResponse<Boolean> response = CustomApiResponse.<Boolean>builder()
                .message("Collaborator check completed")
                .success(true)
                .status(HttpStatus.OK)
                .data(isCollaborator)
                .build();

        return ResponseEntity.ok(response);
    }
    @DeleteMapping
    @Operation(
            summary = "Remove a collaborator from a project",
            description = "Deletes a collaborator by projectId and userId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collaborator removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid projectId or userId"),
                    @ApiResponse(responseCode = "404", description = "Collaborator not found")
            }
    )
    public ResponseEntity<CustomApiResponse<String>> removeCollaborator(
            @RequestParam UUID projectId,
            @RequestParam UUID userId
    ) {
        projectCollaboratorService.removeCollaborator(projectId, userId);

        CustomApiResponse<String> response = CustomApiResponse.<String>builder()
                .message("Collaborator removed successfully")
                .success(true)
                .status(HttpStatus.OK)
                .data("Collaborator with userId " +  projectId + " removed from project " + userId )
                .build();

        return ResponseEntity.ok(response);
    }







}
