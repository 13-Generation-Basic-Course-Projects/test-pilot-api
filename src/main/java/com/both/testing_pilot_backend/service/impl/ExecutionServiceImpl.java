package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.config.RabbitMQConfig;
import com.both.testing_pilot_backend.dto.request.ExecuteBatchRequest;
import com.both.testing_pilot_backend.dto.request.ResolvedExecutionRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.*;
import com.both.testing_pilot_backend.model.enums.ApplicationContextType;
import com.both.testing_pilot_backend.model.enums.ExecutionBatchStatus;
import com.both.testing_pilot_backend.model.enums.ExecutionResultStatus;
import com.both.testing_pilot_backend.model.enums.HttpMethod;
import com.both.testing_pilot_backend.model.enums.TriggerType;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.service.ExecutionService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode; // Import ObjectNode
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionBatchRepository batchRepository;
    private final ExecutionResultRepository resultRepository;

    private final AuthUtils authUtils;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;
    private final RabbitTemplate rabbitTemplate;

    private static final int MAX_CONCURRENT_REQUESTS = 10;

    @Override
    @Transactional
    public Mono<ExecutionBatch> executeTests(ExecuteBatchRequest request, UUID userId) {
        // Validation for 'requestExecution' list being non-null and non-empty is handled by DTO annotations.
        // The 'triggerType', 'triggerSourceId', etc. are now purely informational metadata for the batch record.

        System.out.println("Working in service");
        // 1. Create Initial Batch Record (STARTED)
        ExecutionBatch initialBatch = ExecutionBatch.builder()
                .projectId(request.getProjectId())
                .userId(userId)
                .triggerType(request.getTriggerType())
                .startTimestamp(LocalDateTime.now())
                .overallStatus(ExecutionBatchStatus.STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        System.err.println("After save the batch request "  + initialBatch.toString() );
        return Mono.fromCallable(() -> batchRepository.save(initialBatch))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(savedBatch -> {
                    log.info("Saved batch: {}", savedBatch);
                })
                .doOnError(savedBatch -> {
                    log.info("Cannot save batch: {}", savedBatch);
                })
                .switchIfEmpty(Mono.error(new IllegalStateException("Batch save returned null")))
                .flatMap(savedBatch -> {
                    log.info("Execution batch {} started for project {} trigger type {} with source {}",
                            savedBatch.getBatchId(), savedBatch.getProjectId(), savedBatch.getTriggerType(), savedBatch.getTriggerSourceId());

                    // Publish initial batch status update to RabbitMQ
                    publishExecutionUpdate(savedBatch.getBatchId(), "overall", ExecutionBatchStatus.STARTED.name(), null, null);

                    AtomicInteger executionOrder = new AtomicInteger(0);

                    // 2. Directly execute the pre-computed list of fully resolved execution items
                    return Flux.fromIterable(request.getRequestExecution())
                            .flatMap(item -> {
                                // Call executeSingleRequest with all the necessary info from the ResolvedExecutionRequest item

                                System.out.println("IN iterate request " + item);
                                return executeSingleRequest(
                                        savedBatch,
                                        item.getUrl(),
                                        item.getMethod(),
                                        item.getHeaders(),
                                        item.getBody(),
                                        item.getRequestId(),
                                        item.getTestCaseId(),
                                        executionOrder.getAndIncrement(),
                                        item.getIsExpectedSuccess()
                                );
                            }, MAX_CONCURRENT_REQUESTS)
                            .collectList()
                            .flatMap(results -> {
                                // 3. Update Batch Status (COMPLETED/FAILED)
                                savedBatch.setEndTimestamp(LocalDateTime.now());
                                boolean anyFailed = results.stream().anyMatch(r -> r.getStatus() == ExecutionResultStatus.FAILED || r.getStatus() == ExecutionResultStatus.ERROR);
                                savedBatch.setOverallStatus(anyFailed ? ExecutionBatchStatus.FAILED : ExecutionBatchStatus.COMPLETED);
                                savedBatch.setResults(results);
                                log.info("Saved new batch " + savedBatch);
                                return Mono.fromCallable(() -> {
                                    batchRepository.updateStatusAndEndTime(savedBatch.getBatchId(), savedBatch.getEndTimestamp(), savedBatch.getOverallStatus());
                                    // Publish final batch status update to RabbitMQ
                                    publishExecutionUpdate(savedBatch.getBatchId(), "overall", savedBatch.getOverallStatus().name(), null, null);

                                    return savedBatch;
                                }).subscribeOn(Schedulers.boundedElastic());
                            })
                            .onErrorResume(e -> {
                                log.error("Execution batch {} failed due to error: {}", savedBatch.getBatchId(), e.getMessage(), e);
                                savedBatch.setEndTimestamp(LocalDateTime.now());
                                savedBatch.setOverallStatus(ExecutionBatchStatus.FAILED);
                                return Mono.fromCallable(() -> {
                                            batchRepository.updateStatusAndEndTime(savedBatch.getBatchId(), savedBatch.getEndTimestamp(), savedBatch.getOverallStatus());
                                            // Publish failed batch status update
                                            publishExecutionUpdate(savedBatch.getBatchId(), "overall", savedBatch.getOverallStatus().name(), "Batch execution failed: " + e.getMessage(), null);
                                            // Corrected: Just throw the RuntimeException directly here
                                            throw new RuntimeException("Batch execution failed", e);
                                        }).subscribeOn(Schedulers.boundedElastic())
                                        .cast(ExecutionBatch.class); // Explicit cast to ExecutionBatch, as error signals often get typed to Object
                            });
                });
    }

    /**
     * Executes a single HTTP request using fully resolved details provided in the DTO.
     * This method no longer performs request resolution, but just executes and asserts.
     *
     * @param batch             The parent execution batch.
     * @param resolvedUrl       The fully resolved URL (including protocol, host, path, query).
     * @param resolvedMethod    The HTTP method.
     * @param resolvedHeaders   The HTTP headers.
     * @param resolvedBody      The HTTP request body.
     * @param originalRequestId The original Request ID (for tracking/storage).
     * @param originalTestCaseId The original TestCase ID (nullable, for tracking/storage).
     * @param order             The execution order within the batch.
     * @param isExpectedSuccess The expected success status for this item.
     * @return A Mono emitting the completed ExecutionResult.
     */
    private Mono<ExecutionResult> executeSingleRequest(
            ExecutionBatch batch,
            String resolvedUrl,
            HttpMethod resolvedMethod,
            JsonNode resolvedHeaders,
            JsonNode resolvedBody,
            UUID originalRequestId,
            UUID originalTestCaseId,
            int order,
            boolean isExpectedSuccess
    ) {
            log.info("Start single request");
        return Mono.defer(() -> {
            log.info("Start executing the request");
            LocalDateTime requestStart = LocalDateTime.now();
            long startTimeMillis = System.currentTimeMillis();
            ObjectNode requestNode = objectMapper.createObjectNode()
                    .put("url", resolvedUrl)
                    .put("method", resolvedMethod.name());

            requestNode.set("headers", resolvedHeaders);
            requestNode.set("body", resolvedBody);

            // 1. Prepare ExecutionResult (PENDING/EXECUTING)
            ExecutionResult result = ExecutionResult.builder()
                    .batchId(batch.getBatchId())
                    .requestId(originalRequestId)
                    .testCaseId(originalTestCaseId)
                    .isExpectedSuccess(isExpectedSuccess)
                    .requestDefinitionSnapshot(requestNode)
                    .executionOrder(order)
                    .startTimestamp(requestStart)
                    .status(ExecutionResultStatus.EXECUTING)
                    .createdAt(LocalDateTime.now())
                    .build();

            return Mono.fromCallable(() -> {
                        System.out.println("Reesuultlltltlt " + result.toString());
                        ExecutionResult savedResult =   resultRepository.save(result);
                        publishExecutionUpdate(batch.getBatchId(), "result", ExecutionResultStatus.EXECUTING.name(), null, result.getResultId());
                        return savedResult;
                    }).subscribeOn(Schedulers.boundedElastic())
                    .doOnError(error -> {
                        log.info("Cannot save result " + error);
                    })
                    .doOnSuccess(savedRequest -> {
                        log.info("Saved result " + savedRequest);
                    })
                    .flatMap(savedResult -> {
                        // 3. Build and Execute WebClient Request
                        WebClient client = webClientBuilder.build(); // No baseUrl needed if URI is full URL
                        log.info("Resolve url {} - method {} - body {}", resolvedUrl, resolvedMethod, resolvedBody);
                        return client.method(org.springframework.http.HttpMethod.valueOf(resolvedMethod.name()))
                                .uri(resolvedUrl)
                                .headers(httpHeaders -> {
                                    if (resolvedHeaders != null && resolvedHeaders.isObject()) {
                                        resolvedHeaders.fields().forEachRemaining(entry ->
                                                httpHeaders.add(entry.getKey(), entry.getValue().asText())
                                        );
                                    }
                                })
                                .bodyValue(resolvedBody != null && !resolvedBody.isNull() ? resolvedBody : Mono.empty())
                                .exchangeToMono(clientResponse -> {
                                    System.out.println("Client response the body is in  here " + clientResponse.toString());
                                    // 4. Capture Response Details
                                    long duration = System.currentTimeMillis() - startTimeMillis;
                                    int statusCode = clientResponse.statusCode().value();
                                    System.out.println("working in herer for testing");
                                    HttpHeaders responseHeaders = clientResponse.headers().asHttpHeaders();
                                    System.out.println("Save result in the test case and working in here");

                                    long responseSize = responseHeaders.getContentLength();
                                    if (responseSize == -1) responseSize = 0;

                                    // Capture requestSentDetails (what was actually sent)
                                    ObjectNode requestSent = objectMapper.createObjectNode();
                                    requestSent.put("method", resolvedMethod.name());
                                    requestSent.put("url", clientResponse.request().getURI().toString());
                                    requestSent.set("headers", objectMapper.valueToTree(clientResponse.request().getHeaders()));
                                    requestSent.set("body", resolvedBody);

                                    long finalResponseSize = responseSize;
                                    return clientResponse.bodyToMono(String.class)
                                            .defaultIfEmpty("")
                                            .map(body -> {
                                                // 5. Perform Assertions and Determine Status
                                                ExecutionResultStatus finalStatus;
                                                ObjectNode assertionResults = objectMapper.createObjectNode();

                                                boolean isSuccessStatusCode = (statusCode >= 100 && statusCode < 400);

                                                if (savedResult.getIsExpectedSuccess()) {
                                                    if (isSuccessStatusCode) {
                                                        finalStatus = ExecutionResultStatus.PASSED;
                                                        assertionResults.put("statusCodeAssertion", "PASSED: Expected success, got " + statusCode);
                                                    } else {
                                                        finalStatus = ExecutionResultStatus.FAILED;
                                                        assertionResults.put("statusCodeAssertion", "FAILED: Expected success, got " + statusCode);
                                                    }
                                                } else {
                                                    if (!isSuccessStatusCode) {
                                                        finalStatus = ExecutionResultStatus.PASSED;
                                                        assertionResults.put("statusCodeAssertion", "PASSED: Expected failure, got " + statusCode);
                                                    } else {
                                                        finalStatus = ExecutionResultStatus.FAILED;
                                                        assertionResults.put("statusCodeAssertion", "FAILED: Expected failure, got " + statusCode);
                                                    }
                                                }

                                                // TODO: Implement more complex body/header assertions based on testLogicJson
                                                // The 'testLogicJson' contains 'expectedResponse.bodyAssertions', 'expectedResponse.headerAssertions'
                                                // if they were provided in the RequestTestCase link's 'testLogic'.
                                                // You would use JsonPath (e.g., com.jayway.jsonpath.JsonPath) to extract values from 'body' and compare.


                                                // Populate and save result
                                                savedResult.setEndTimestamp(LocalDateTime.now());
                                                savedResult.setStatus(finalStatus);
                                                savedResult.setRequestSentDetails(requestSent);
                                                savedResult.setResponseStatusCode(statusCode);
                                                savedResult.setResponseHeaders(objectMapper.valueToTree(responseHeaders.toSingleValueMap()));
                                                savedResult.setResponseBody(body);
                                                savedResult.setResponseSizeBytes(finalResponseSize);
                                                savedResult.setDurationMs((int) duration);
                                                savedResult.setAssertionResults(assertionResults);


                                                resultRepository.update(savedResult);
                                                log.info("Request execution {} completed with status {}", savedResult.getResultId(), finalStatus);
                                                return savedResult;
                                            });
                                })
                                .onErrorResume(e -> {
                                    log.error("Error executing request {}: {}", originalRequestId, e.getMessage(), e);
                                    savedResult.setEndTimestamp(LocalDateTime.now());
                                    savedResult.setStatus(ExecutionResultStatus.ERROR);
                                    savedResult.setResponseBody("Error during execution: " + e.getMessage());
                                    savedResult.setAssertionResults(objectMapper.createObjectNode().put("executionError", e.getMessage()));
                                    savedResult.setDurationMs((int) (System.currentTimeMillis() - startTimeMillis));
                                    resultRepository.update(savedResult);
                                    return Mono.just(savedResult);
                                });
                    });
        });
    }

    @Override
    public Mono<ExecutionBatch> getBatchResults(UUID batchId) {
        return Mono.fromCallable(() -> batchRepository.findById(batchId))
                .switchIfEmpty(Mono.error(new NotFoundException("Execution Batch not found with ID: " + batchId)))
                .flatMap(batch -> Mono.fromCallable(() -> {
                    // Enrich batch with results. Fetch results using resultRepository.findByBatchId
                    List<ExecutionResult> results = resultRepository.findByBatchId(batch.getBatchId());
                    // This requires a DTO for ExecutionBatch that includes List<ExecutionResult>
                    // For now, if no such DTO, just log and return the batch.
                    log.debug("Fetched batch {} with {} results.", batchId, results.size());
                    // Alternatively, return a custom DTO that includes batch and results:
                    // return new ExecutionBatchWithResultsDTO(batch, results);
                    return batch;
                })).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public List<ExecutionBatch> getAllBatchesForProject(UUID projectId) {
        return  batchRepository.findByProjectId(projectId);
    }

    @Override
    public Mono<List<ExecutionBatch>> getAllBatches() {
        return Mono.fromCallable(batchRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ExecutionResult> getExecutionResultDetails(UUID resultId) {
        return Mono.fromCallable(() -> resultRepository.findById(resultId))
                .switchIfEmpty(Mono.error(new NotFoundException("Execution Result not found with ID: " + resultId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void publishExecutionUpdate(UUID batchId, String updateType, String status, String message, UUID resultId) {
        ObjectNode updateMessage = objectMapper.createObjectNode();
        updateMessage.put("batchId", batchId.toString());
        updateMessage.put("updateType", updateType);
        updateMessage.put("status", status);
        if (message != null) {
            updateMessage.put("message", message);
        }
        if (resultId != null) {
            updateMessage.put("resultId", resultId.toString());
        }
        updateMessage.put("timestamp", LocalDateTime.now().toString());

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXECUTION_UPDATES_QUEUE, updateMessage.toString());
        log.debug("Published update to RabbitMQ: {}", updateMessage.toString());
    }
}
