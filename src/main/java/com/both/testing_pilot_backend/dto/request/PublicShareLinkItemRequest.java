package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for public share link")
public class PublicShareLinkItemRequest {

    @NotBlank(message = "Item type is required")
    @Schema(
        description = "Type of the item being shared (e.g., 'COLLECTION', 'PROJECT')",
        example = "COLLECTION"
    )
    private String itemType;

    @NotNull(message = "Item ID is required")
    @Schema(
            description = "Unique identifier of the item being shared",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID itemId;

    @NotNull(message = "Share link ID is required")
    @Schema(
            description = "ID of the public share link this item belongs to",
            example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8"
    )
    private UUID shareLinkId;
}