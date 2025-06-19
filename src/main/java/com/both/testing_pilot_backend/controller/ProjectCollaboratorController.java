package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.dto.response.ProjectCollaboratorDTO;
import com.both.testing_pilot_backend.model.ProjectCollaborator;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collaborators")
@Tag(name = "Project Collaborator", description = "Operations related to inviting and managing project collaborators")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProjectCollaboratorController {
    private final ProjectCollaboratorService projectCollaboratorService;

    @PostMapping("/invite")
    @Operation(
            summary = "Invite a new collaborator to a project",
            description = "Sends an invitation email to a user to become a collaborator on a project. Only the project owner can invite."
    )
    @PreAuthorize("@projectSecurity.isProjectOwner(#request.projectId)")
    public ResponseEntity<CustomApiResponse<?>> invite(@Valid @RequestBody ProjectCollaboratorRequest request) {
        projectCollaboratorService.inviteCollaborator(request);

        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Invitation sent successfully to " + request.getCollaboratorEmail() + ". Please check their email for verification link.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/verify")
    @Operation(
            summary = "Verify collaborator invitation via link",
            description = "Verifies a project collaborator invitation using a unique JWT token from the email link. The user must be logged in with the invited email."
    )
    public ResponseEntity<CustomApiResponse<?>> verify(
            @RequestParam("token") String token) {
        projectCollaboratorService.verifyCollaboratorInvite(token);

        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Collaborator verified successfully. You are now a collaborator on the project.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/by-project/{projectId}")
    @Operation(
            summary = "Get collaborators by project ID",
            description = "Returns the list of collaborators associated with the specified project ID."
    )
    public ResponseEntity<CustomApiResponse<List<ProjectCollaboratorDTO>>> getCollaboratorsByProjectId(
            @PathVariable("projectId") UUID projectId) {

        List<ProjectCollaboratorDTO> collaborators = projectCollaboratorService.getCollaboratorByProjectId(projectId);


        CustomApiResponse<List<ProjectCollaboratorDTO>> apiResponse = CustomApiResponse.<List<ProjectCollaboratorDTO>>builder()
                .message("Collaborators retrieved successfully.")
                .payload(collaborators)
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a collaborator from a project",
            description = "Removes a collaborator from the project using their projectCollaboratorId. Only the project owner can perform this action."
    )
    public ResponseEntity<CustomApiResponse<?>> delete(@PathVariable("id") UUID id) { // This ID is projectCollaboratorId
        projectCollaboratorService.deleteCollaborator(id);

        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Collaborator deleted successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .timestamps(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
