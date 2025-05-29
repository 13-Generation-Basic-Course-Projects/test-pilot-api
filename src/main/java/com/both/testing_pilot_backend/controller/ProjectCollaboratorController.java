package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.dto.response.CustomApiResponse;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/collaborators")
@RequiredArgsConstructor
public class ProjectCollaboratorController {

    private final ProjectCollaboratorService projectCollaboratorService;


    @PostMapping("/invite")
    public ResponseEntity<CustomApiResponse<UUID>> inviteCollaborator(
            @Valid @RequestBody ProjectCollaboratorRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication name: " + authentication.getName());

        UUID inviterUserId;
        try {
            inviterUserId = UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(CustomApiResponse.<UUID>builder()
                            .message("Invalid user ID in authentication")
                            .success(false)
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        UUID collaboratorId = projectCollaboratorService.inviteUserToProject(
                request.getProjectId(),
                inviterUserId,
                request.getCollaboratorEmail()
        );

        CustomApiResponse<UUID> response = CustomApiResponse.<UUID>builder()
                .message("Collaborator invited successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .data(collaboratorId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
