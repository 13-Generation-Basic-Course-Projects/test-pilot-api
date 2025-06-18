package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request DTO for inviting a project collaborator")
public class ProjectCollaboratorRequest {

    @NotNull(message = "Project ID cannot be null")
    @Schema(description = "ID of the project to invite collaborator to", example = "a1b2c3d4-e5f6-7777-1234-567890abcdef")
    private UUID projectId;

    @NotBlank(message = "Collaborator email cannot be blank")
    @Email(message = "Collaborator email should be a valid email format")
    @Schema(description = "Email of the user to invite as a collaborator", example = "collaborator@example.com")
    private String collaboratorEmail;
}
