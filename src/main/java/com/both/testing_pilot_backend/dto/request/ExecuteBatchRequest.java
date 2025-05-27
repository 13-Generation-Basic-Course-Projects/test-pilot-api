// src/main/java/com/both/testing_pilot_backend/dto/request/ExecuteBatchRequest.java
package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.model.enums.TriggerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Base request for initiating an execution batch")
public class ExecuteBatchRequest {
    @NotNull(message = "Project ID cannot be null for an execution batch.")
    @Schema(description = "ID of the project to which this execution batch belongs.", example = "cc22ee9d-779e-400b-be2d-130e54816723")
    private UUID projectId;

    @NotNull(message = "Trigger type cannot be null")
    @Schema(description = "Type of trigger for the batch execution", example = "SINGLE_REQUEST",
            allowableValues = {"SINGLE_TEST_CASE", "REQUEST_TEST_CASES", "FOLDER_TEST_CASES", "COLLECTION_TEST_CASES", "PROJECT_TEST_CASES", "SELECTED_TEST_CASES", "SINGLE_REQUEST", "SELECTED_REQUESTS"})
    private TriggerType triggerType;

    @Schema(description = "ID of the single entity that triggered the batch (e.g., Request ID, Project ID). Nullable if selectedItemIds is used.", example = "123e4567-e89b-12d3-a456-000000000001", nullable = true)
    private UUID triggerSourceId;

    @Schema(description = "List of IDs for selected items when triggerType is SELECTED_REQUESTS or SELECTED_TEST_CASES.", example = "[\"123e4567-e89b-12d3-a456-000000000001\", \"123e4567-e89b-12d3-a456-000000000002\"]", nullable = true)
    private List<UUID> selectedItemIds;

    @Schema(description = "Optional: Request ID to run a single test case against (used with SINGLE_TEST_CASE triggerType).", example = "123e4567-e89b-12d3-a456-000000000001", nullable = true)
    private UUID requestId;
}
