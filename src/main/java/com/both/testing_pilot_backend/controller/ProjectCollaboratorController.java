package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse; // Import CustomApiResponse
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

    @PostMapping("/invite")
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
    public ResponseEntity<CustomApiResponse<?>> invite(@Valid @RequestBody ProjectCollaboratorRequest request) { // Added @Valid
        projectCollaboratorService.inviteCollaborator(request);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Invitation sent successfully to " + request.getCollaboratorEmail() + ".")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/verify/{id}")
    @Operation(
            summary = "Verify collaborator invitation",
            description = "Verifies a project collaborator invitation using the provided ID and OTP code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collaborator verified successfully",
                            content = @Content(schema = @Schema(implementation = CustomApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid verification code",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (not the intended recipient)",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "Invitation not found or expired",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    public ResponseEntity<CustomApiResponse<?>> verify(
            @PathVariable("id") UUID id,
            @RequestParam("code") String code) {
        projectCollaboratorService.verifyCollaboratorInvite(id, code);
        CustomApiResponse<?> apiResponse = CustomApiResponse.builder()
                .message("Collaborator verified successfully.")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
