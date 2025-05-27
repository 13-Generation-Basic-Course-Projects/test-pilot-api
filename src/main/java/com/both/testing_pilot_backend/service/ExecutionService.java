// src/main/java/com/both/testing_pilot_backend/service/ExecutionService.java
package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ExecuteBatchRequest;
import com.both.testing_pilot_backend.model.ExecutionBatch;
import com.both.testing_pilot_backend.model.ExecutionResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ExecutionService {
    Mono<ExecutionBatch> executeTests(ExecuteBatchRequest request, UUID userId);
    Mono<ExecutionBatch> getBatchResults(UUID batchId);
    Mono<List<ExecutionBatch>> getAllBatchesForProject(UUID projectId);
    Mono<List<ExecutionBatch>> getAllBatches();
    Mono<ExecutionResult> getExecutionResultDetails(UUID resultId);
}
