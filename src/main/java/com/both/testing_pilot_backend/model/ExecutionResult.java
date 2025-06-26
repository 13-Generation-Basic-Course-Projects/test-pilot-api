package com.both.testing_pilot_backend.model;

import com.both.testing_pilot_backend.model.enums.ExecutionResultStatus;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {
    private UUID resultId;
    private UUID batchId;
    private UUID requestId;
    private UUID testCaseId;
    private TestCase testCase;
    private Boolean isExpectedSuccess;
    private JsonNode requestDefinitionSnapshot;
    private Integer executionOrder;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private ExecutionResultStatus status;
    private JsonNode requestSentDetails;
    private Integer responseStatusCode;
    private JsonNode responseHeaders;
    private String responseBody;
    private Long responseSizeBytes;
    private Integer durationMs;
    private JsonNode assertionResults;
    private LocalDateTime createdAt;
}
