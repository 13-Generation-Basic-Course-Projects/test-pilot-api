package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a public share link")
public class PublicShareLinkRequest {

    @NotBlank(message = "Token is required")
    @Schema(
        description = "Unique token for the shared link",
        example = "abc123xyz")
    private String token;

    @NotBlank(message = "Shared item type is required")
    @Schema(
            description = "Type of the shared item (e.g., 'COLLECTION', 'PROJECT')",
            example = "COLLECTION")
    private String sharedItemType;

    @NotNull(message = "Shared item ID is required")
    @Schema(
            description = "ID of the shared item",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID sharedItemId;

    @Future(message = "Expiration time must be in the future")
    @NotNull(message = "Expiration time is required")
    @Schema(
            description = "Expiration date and time of the shared link",
            example = "2025-12-31T23:59:59")
    private LocalDateTime expireAt;
}