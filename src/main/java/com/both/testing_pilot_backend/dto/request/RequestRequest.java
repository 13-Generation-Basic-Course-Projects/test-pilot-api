package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.model.HttpMethod;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request DTO for creating and updating requests")
public class RequestRequest {
    @NotBlank(message = "Request name cannot be blank")
    @Schema(description = "Name of the request", example = "Get All Users")
    private String name;

    @NotNull(message = "Collection ID cannot be null")
    @Schema(description = "ID of the collection this request belongs to", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID collectionId;

    @NotNull(message = "HTTP Method cannot be null")
    @Schema(description = "HTTP method of the request", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"})
    private HttpMethod method;

    @NotNull(message = "Details cannot be null")
    @Schema(description = "JSONB field for request details (headers, body, query params, etc.)")
    private JsonNode details;
}
