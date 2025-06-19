package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for defining a project-level variable")
public class VariableRequest {

    @NotBlank(message = "Key name cannot be blank")
    @Size(min = 1, max = 255, message = "Key name must be between 1 and 255 characters")
    @Schema(description = "Name of the variable key", example = "API_TOKEN")
    private String keyName;

    @NotBlank(message = "Key value cannot be blank")
    @Size(max = 1024, message = "Key value must not exceed 1024 characters")
    @Schema(description = "Value associated with the key", example = "s3cr3t@value!")
    private String keyValue;

    @Schema(description = "Flag indicating whether this variable is enabled", example = "true")
    private boolean enabled;

    @NotNull(message = "Project ID cannot be null")
    @Schema(description = "ID of the project this variable belongs to", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID projectId;
}
