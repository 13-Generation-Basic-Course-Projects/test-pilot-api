package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.model.enums.TriggerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
    @Schema(description = "Type of trigger for the batch execution (now primarily informational).", example = "SELECTED_TEST_CASES",
            allowableValues = {"SINGLE_TEST_CASE", "REQUEST_TEST_CASES", "FOLDER_TEST_CASES", "COLLECTION_TEST_CASES", "PROJECT_TEST_CASES", "SELECTED_TEST_CASES", "SINGLE_REQUEST", "SELECTED_REQUESTS"})
    private TriggerType triggerType;

    @NotNull(message = "Request execution list cannot be null")
    @NotEmpty(message = "At least one request execution item must be provided")
    @Schema(description = "The pre-computed list of all fully resolved HTTP requests and their test details for this batch. Frontend is responsible for populating this list.", type = "array")
    private List<ResolvedExecutionRequest> requestExecution;
}
