package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for creating and updating collections")
public class CollectionRequest {

    @NotBlank(message = "Collection name cannot be blank")
    @Size(min = 3, max = 255, message = "Collection name must be between 3 and 255 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N} _'\"!?.,:()\\[\\]-]{3,255}$", message = "Collection name contains invalid characters")
    @Schema(description = "Name of the collection", example = "My First Collection")
    private String name;

    @NotNull(message = "Project ID cannot be null")
    @Schema(description = "ID of the project this collection belongs to", example = "a1b2c3d4-e5f6-7777-1234-567890abcdef")
    private UUID projectId;
}
