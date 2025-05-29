package com.both.testing_pilot_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCollaboratorRequest {
    @NotNull(message = "Project ID is required")
    private UUID projectId;

    @NotNull(message = "Collaborator email is required")
    @Email(message = "Invalid email format")
    private String collaboratorEmail;

}