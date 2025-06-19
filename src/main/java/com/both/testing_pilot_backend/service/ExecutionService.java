package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.ExecuteBatchRequest;
import com.both.testing_pilot_backend.model.ExecutionBatch;
import com.both.testing_pilot_backend.model.ExecutionResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ExecutionService {
    Mono<ExecutionBatch> executeTests(ExecuteBatchRequest request, UUID userId); // Now synchronous
    Mono<ExecutionBatch> getBatchResults(UUID batchId); // Now synchronous
    List<ExecutionBatch> getAllBatchesForProject(UUID projectId); // Now synchronous
    Mono<List<ExecutionBatch>> getAllBatches(); // Now synchronous
    Mono<ExecutionResult> getExecutionResultDetails(UUID resultId); // Now synchronous
}
