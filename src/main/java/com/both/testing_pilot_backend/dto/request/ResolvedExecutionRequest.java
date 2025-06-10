package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.model.enums.HttpMethod;
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
@Schema(description = "Represents a single, fully resolved HTTP request ready for execution, including its associated test case details.")
public class ResolvedExecutionRequest {
    @NotBlank(message = "URL cannot be blank for a resolved execution request.")
    @Schema(description = "The fully resolved URL path for the HTTP request (e.g., /api/users/123).", example = "/api/users/123")
    private String url;

    @NotNull(message = "HTTP Method cannot be null for a resolved execution request.")
    @Schema(description = "The HTTP method of the resolved request.", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE"})
    private HttpMethod method;

    @NotNull(message = "Headers cannot be null for a resolved execution request.")
    @Schema(description = "The resolved HTTP headers for the request.", example = "{\"Content-Type\": \"application/json\"}")
    private JsonNode headers;

    @Schema(description = "The resolved HTTP request body. Can be null for GET/DELETE.", example = "{\"email\": \"test@example.com\"}", nullable = true)
    private JsonNode body;

    // --- Original Request/TestCase Link Details (for tracking in ExecutionResult) ---
    @NotNull(message = "Original Request ID cannot be null for a resolved execution request.")
    @Schema(description = "The original ID of the base Request blueprint for this execution.", example = "123e4567-e89b-12d3-a456-000000000001")
    private UUID requestId;

    @Schema(description = "ID of the TestCase that was applied. Null if executing the base request without a specific test case.", example = "f1e2d3c4-b5a6-7890-1234-000000000001", nullable = true)
    private UUID testCaseId;

    @NotNull(message = "Is Expected Success cannot be null for a resolved execution request.")
    @Schema(description = "Whether this specific execution item is expected to result in a successful HTTP status (1xx-3xx).", example = "true")
    private Boolean isExpectedSuccess;
}
