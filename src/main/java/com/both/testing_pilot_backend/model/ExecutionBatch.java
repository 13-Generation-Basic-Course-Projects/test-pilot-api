package com.both.testing_pilot_backend.model;

import com.both.testing_pilot_backend.model.enums.ExecutionBatchStatus;
import com.both.testing_pilot_backend.model.enums.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionBatch {
    private UUID batchId;
    private UUID projectId;
    private UUID userId;
    private TriggerType triggerType;
    private UUID triggerSourceId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private ExecutionBatchStatus overallStatus;
    private List<ExecutionResult> results;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

