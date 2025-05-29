package com.both.testing_pilot_backend.controller;

import com.both.testing_pilot_backend.dto.request.ProjectCollaboratorRequest;
import com.both.testing_pilot_backend.service.ProjectCollaboratorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/collaborators")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProjectCollaboratorController {

    private final ProjectCollaboratorService projectCollaboratorService;

    @PostMapping("/invite")
    public ResponseEntity<String> invite(@RequestBody ProjectCollaboratorRequest request) {
        projectCollaboratorService.inviteCollaborator(request);
        return ResponseEntity.ok("Invitation sent.");
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<String> verify(@PathVariable("id") UUID id) {
        projectCollaboratorService.verifyCollaboratorInvite(id);
        return ResponseEntity.ok("Collaborator verified.");
    }
}
