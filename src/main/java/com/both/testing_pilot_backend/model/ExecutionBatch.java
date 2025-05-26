package com.both.testing_pilot_backend.model;

import com.both.testing_pilot_backend.model.enums.ExecutionBatchStatus;
import com.both.testing_pilot_backend.model.enums.TriggerType;
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
public class ExecutionBatch {
    private Long batchId;
    private Integer workspaceId;
    private UUID userId;
    private Integer environmentId;
    private TriggerType triggerType;
    private Integer triggerSourceId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private ExecutionBatchStatus overallStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
