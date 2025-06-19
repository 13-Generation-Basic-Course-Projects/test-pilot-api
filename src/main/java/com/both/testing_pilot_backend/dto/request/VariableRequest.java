package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class VariableRequest {

    private String keyName;

    private String keyValue;

    @NotNull(message = "Enabled flag must be specified as 'true' or 'false'.")
    @Schema(description = "Whether the collection is enabled", example = "true")
    private Boolean enabled;

    @NotNull(message = "Project ID cannot be null")
    @Schema(description = "ID of the project this collection belongs to", example = "a1b2c3d4-e5f6-7777-1234-567890abcdef")
    private UUID projectId;
}
