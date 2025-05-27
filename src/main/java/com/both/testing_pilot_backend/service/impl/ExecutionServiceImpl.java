// src/main/java/com/both/testing_pilot_backend/service/impl/ExecutionServiceImpl.java
package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.ExecuteBatchRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.*;
import com.both.testing_pilot_backend.model.enums.ExecutionBatchStatus;
import com.both.testing_pilot_backend.model.enums.ExecutionResultStatus;
import com.both.testing_pilot_backend.model.enums.TriggerType;
import com.both.testing_pilot_backend.repository.*;
import com.both.testing_pilot_backend.service.ExecutionService;
import com.both.testing_pilot_backend.utils.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionBatchRepository batchRepository;
    private final ExecutionResultRepository resultRepository;
    private final RequestRepository requestRepository;
    private final TestCaseRepository testCaseRepository;
    private final CollectionsRepository collectionRepository;
    private final ProjectRepository projectRepository;
    private final RequestTestCaseRepository requestTestCaseRepository; // NEW: Inject RequestTestCaseRepository
    private final AuthUtils authUtils;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    // The API_ASSERTION_DATA_TYPE_ID is no longer primarily for linking, but might be useful for filtering what TestCases are displayed/created.
    // private static final UUID API_ASSERTION_DATA_TYPE_ID = UUID.fromString("f1e2d3c4-b5a6-7890-1234-567890abcdef");

    @Override
    @Transactional
    public Mono<ExecutionBatch> executeTests(ExecuteBatchRequest request, UUID userId) {
        switch (request.getTriggerType()) {
            case SINGLE_REQUEST:
            case REQUEST_TEST_CASES:
            case COLLECTION_TEST_CASES:
            case PROJECT_TEST_CASES:
                if (request.getTriggerSourceId() == null) {
                    return Mono.error(new IllegalArgumentException("For trigger type " + request.getTriggerType() + ", 'triggerSourceId' cannot be null."));
                }
                break;
            case SINGLE_TEST_CASE: // triggerSourceId is TestCase ID, requestId is Request ID
                if (request.getTriggerSourceId() == null || request.getRequestId() == null) {
                    return Mono.error(new IllegalArgumentException("For SINGLE_TEST_CASE, 'triggerSourceId' (test case ID) and 'requestId' (base request ID) must be provided."));
                }
                break;
            case SELECTED_REQUESTS: // selectedItemIds are Request IDs
            case SELECTED_TEST_CASES: // selectedItemIds are RequestTestCase IDs
                if (request.getSelectedItemIds() == null || request.getSelectedItemIds().isEmpty()) {
                    return Mono.error(new IllegalArgumentException("For trigger type " + request.getTriggerType() + ", 'selectedItemIds' cannot be empty."));
                }
                break;
            default:
                // FOLDER_TEST_CASES or any other unsupported type
                return Mono.error(new IllegalArgumentException("Unsupported trigger type: " + request.getTriggerType()));
        }

        // 1. Create Initial Batch Record (STARTED)
        ExecutionBatch initialBatch = ExecutionBatch.builder()
                .projectId(request.getProjectId())
                .userId(userId)
                .triggerType(request.getTriggerType())
                .triggerSourceId(request.getTriggerSourceId())
                .startTimestamp(LocalDateTime.now())
                .overallStatus(ExecutionBatchStatus.STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the batch and then proceed with execution
        return Mono.fromCallable(() -> batchRepository.save(initialBatch))
                .flatMap(savedBatch -> {
                    log.info("Execution batch {} started for project {} trigger type {} with source {}",
                            savedBatch.getBatchId(), savedBatch.getProjectId(), savedBatch.getTriggerType(), savedBatch.getTriggerSourceId());

                    Mono<List<ExecutionResult>> executionFlow;
                    AtomicInteger executionOrder = new AtomicInteger(0);

                    // 2. Determine Requests & Test Cases to Execute based on TriggerType
                    // Now, we'll primarily fetch RequestTestCase links for combined execution
                    switch (request.getTriggerType()) {
                        case SINGLE_REQUEST:
                            // Execute a single Request, without any specific linked test case (default isExpectedSuccess = true)
                            executionFlow = Mono.justOrEmpty(requestRepository.findById(request.getTriggerSourceId()))
                                    .switchIfEmpty(Mono.error(new NotFoundException("Request not found with ID: " + request.getTriggerSourceId())))
                                    .flatMap(req -> executeSingleRequest(savedBatch, req, null, executionOrder.getAndIncrement(), true))
                                    .map(List::of); // Wrap single result in a list
                            break;

                        case SINGLE_TEST_CASE:
                            // Execute a specific TestCase against a specific Request using a RequestTestCase link
                            // triggerSourceId is test_case_id, requestId is request_id
                            executionFlow = Mono.justOrEmpty(requestTestCaseRepository.findByRequestIdAndTestCaseId(request.getRequestId(), request.getTriggerSourceId()))
                                    .switchIfEmpty(Mono.error(new NotFoundException("RequestTestCase link not found for Request ID: " + request.getRequestId() + " and Test Case ID: " + request.getTriggerSourceId())))
                                    .flatMap(requestTestCase -> executeSingleRequest(savedBatch, requestTestCase.getRequest(), requestTestCase.getTestCase(), executionOrder.getAndIncrement(), requestTestCase.isExpectedSuccess()))
                                    .map(List::of);
                            break;


                        case REQUEST_TEST_CASES:
                            // Fetch a Request and all its linked RequestTestCase entries
                            executionFlow = Mono.justOrEmpty(requestRepository.findById(request.getTriggerSourceId()))
                                    .switchIfEmpty(Mono.error(new NotFoundException("Request not found with ID: " + request.getTriggerSourceId())))
                                    .flatMapMany(req -> {
                                        List<RequestTestCase> requestTestCases = requestTestCaseRepository.findByRequestId(req.getId());

                                        if (requestTestCases.isEmpty()) {
                                            // If no linked test cases, execute the base request once without a specific test case
                                            return executeSingleRequest(savedBatch, req, null, executionOrder.getAndIncrement(), true).flux();
                                        } else {
                                            // Execute each RequestTestCase link
                                            return Flux.fromIterable(requestTestCases)
                                                    .flatMap(requestTestCase -> executeSingleRequest(savedBatch, requestTestCase.getRequest(), requestTestCase.getTestCase(), executionOrder.getAndIncrement(), requestTestCase.isExpectedSuccess()));
                                        }
                                    })
                                    .collectList();
                            break;

                        case SELECTED_REQUESTS:
                            // Execute specific Requests, without any linked test cases
                            executionFlow = Flux.fromIterable(request.getSelectedItemIds())
                                    .flatMap(requestId -> Mono.justOrEmpty(requestRepository.findById(requestId))
                                            .switchIfEmpty(Mono.error(new NotFoundException("Selected Request not found with ID: " + requestId)))
                                            .flatMap(req -> executeSingleRequest(savedBatch, req, null, executionOrder.getAndIncrement(), true)))
                                    .collectList();
                            break;

                        case SELECTED_TEST_CASES:
                            // Execute specific RequestTestCase links
                            executionFlow = Flux.fromIterable(request.getSelectedItemIds())
                                    .flatMap(requestTestCaseId -> Mono.justOrEmpty(requestTestCaseRepository.findById(requestTestCaseId))
                                            .switchIfEmpty(Mono.error(new NotFoundException("Selected RequestTestCase link not found with ID: " + requestTestCaseId)))
                                            .flatMap(requestTestCase -> executeSingleRequest(savedBatch, requestTestCase.getRequest(), requestTestCase.getTestCase(), executionOrder.getAndIncrement(), requestTestCase.isExpectedSuccess()))
                                    )
                                    .collectList();
                            break;

                        case COLLECTION_TEST_CASES:
                            // Fetch collection, then all requests in collection, then all linked RequestTestCase entries for each request
                            executionFlow = Mono.justOrEmpty(collectionRepository.getCollectionsById(request.getTriggerSourceId()))
                                    .switchIfEmpty(Mono.error(new NotFoundException("Collection not found with ID: " + request.getTriggerSourceId())))
                                    .flatMapMany(col -> {
                                        List<Request> requestsInCollection = requestRepository.findByCollectionId(col.getCollectionsId());
                                        return Flux.fromIterable(requestsInCollection)
                                                .flatMap(req -> {
                                                    List<RequestTestCase> requestTestCases = requestTestCaseRepository.findByRequestId(req.getId());
                                                    if (requestTestCases.isEmpty()) {
                                                        return executeSingleRequest(savedBatch, req, null, executionOrder.getAndIncrement(), true).flux();
                                                    } else {
                                                        return Flux.fromIterable(requestTestCases)
                                                                .flatMap(requestTestCase -> executeSingleRequest(savedBatch, requestTestCase.getRequest(), requestTestCase.getTestCase(), executionOrder.getAndIncrement(), requestTestCase.isExpectedSuccess()));
                                                    }
                                                });
                                    })
                                    .collectList();
                            break;

                        case PROJECT_TEST_CASES:
                            // Fetch project, then all collections in project, then all requests in collections, then all linked RequestTestCase entries
                            executionFlow = Mono.justOrEmpty(projectRepository.findByProjectId(request.getTriggerSourceId()))
                                    .switchIfEmpty(Mono.error(new NotFoundException("Project not found with ID: " + request.getTriggerSourceId())))
                                    .flatMapMany(proj -> {
                                        List<Collections> collectionsInProject = collectionRepository.findByProjectId(proj.getProjectId());
                                        return Flux.fromIterable(collectionsInProject)
                                                .flatMap(col -> {
                                                    List<Request> requestsInCollection = requestRepository.findByCollectionId(col.getCollectionsId());
                                                    return Flux.fromIterable(requestsInCollection)
                                                            .flatMap(req -> {
                                                                List<RequestTestCase> requestTestCases = requestTestCaseRepository.findByRequestId(req.getId());
                                                                if (requestTestCases.isEmpty()) {
                                                                    return executeSingleRequest(savedBatch, req, null, executionOrder.getAndIncrement(), true).flux();
                                                                } else {
                                                                    return Flux.fromIterable(requestTestCases)
                                                                            .flatMap(requestTestCase -> executeSingleRequest(savedBatch, requestTestCase.getRequest(), requestTestCase.getTestCase(), executionOrder.getAndIncrement(), requestTestCase.isExpectedSuccess()));
                                                                }
                                                            });
                                                });
                                    })
                                    .collectList();
                            break;

                        case FOLDER_TEST_CASES:
                            executionFlow = Mono.error(new IllegalArgumentException("FOLDER_TEST_CASES trigger type not yet implemented."));
                            break;

                        default:
                            executionFlow = Mono.error(new IllegalArgumentException("Unsupported trigger type: " + request.getTriggerType()));
                            break;
                    }

                    // Execute all determined requests/test cases and then update batch status
                    return executionFlow
                            .flatMap(results -> {
                                // 3. Update Batch Status (COMPLETED/FAILED)
                                savedBatch.setEndTimestamp(LocalDateTime.now());
                                boolean anyFailed = results.stream().anyMatch(r -> r.getStatus() == ExecutionResultStatus.FAILED || r.getStatus() == ExecutionResultStatus.ERROR);
                                savedBatch.setOverallStatus(anyFailed ? ExecutionBatchStatus.FAILED : ExecutionBatchStatus.COMPLETED);

                                return Mono.fromCallable(() -> {
                                    batchRepository.updateStatusAndEndTime(savedBatch.getBatchId(), savedBatch.getEndTimestamp(), savedBatch.getOverallStatus());
                                    return savedBatch;
                                });
                            })
                            .onErrorResume(e -> {
                                // 4. Handle Batch Failure/Abortion due to an error during execution or setup
                                log.error("Execution batch {} failed due to error: {}", savedBatch.getBatchId(), e.getMessage(), e);
                                savedBatch.setEndTimestamp(LocalDateTime.now());
                                savedBatch.setOverallStatus(ExecutionBatchStatus.FAILED); // Or ABORTED
                                return Mono.fromCallable(() -> {
                                    batchRepository.updateStatusAndEndTime(savedBatch.getBatchId(), savedBatch.getEndTimestamp(), savedBatch.getOverallStatus());
                                    throw new RuntimeException("Batch execution failed", e);
                                }).flatMap(x -> Mono.error((Throwable) x));
                            });
                });
    }

    private Mono<ExecutionResult> executeSingleRequest(ExecutionBatch batch, Request baseRequest, TestCase testCase, int order, boolean isExpectedSuccess) {
        return Mono.defer(() -> {
            LocalDateTime requestStart = LocalDateTime.now();
            long startTimeMillis = System.currentTimeMillis();
            JsonNode testCaseValueJson = null; // Still needed for requestOverrides and bodyAssertions from TestCase.value
            if (testCase != null && testCase.getValue() != null) {
                try {
                    testCaseValueJson = objectMapper.readTree(testCase.getValue());
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse TestCase value for testCaseId {}: {}", testCase.getId(), e.getMessage());
                }
            }

            // 1. Prepare ExecutionResult (PENDING/EXECUTING)
            ExecutionResult result = ExecutionResult.builder()
                    .resultId(UUID.randomUUID())
                    .batchId(batch.getBatchId())
                    .requestId(baseRequest.getId())
                    .testCaseId(testCase != null ? testCase.getId() : null)
                    .isExpectedSuccess(isExpectedSuccess)
                    .requestDefinitionSnapshot(baseRequest.getDetails())
                    .executionOrder(order)
                    .startTimestamp(requestStart)
                    .status(ExecutionResultStatus.EXECUTING)
                    .createdAt(LocalDateTime.now())
                    .build();

            JsonNode finalTestCaseValueJson = testCaseValueJson;
            return Mono.fromCallable(() -> {
                resultRepository.save(result);
                return result;
            }).flatMap(savedResult -> {
                // 2. Resolve Dynamic Request Details (headers, query params, body)
                JsonNode resolvedRequestDetails = resolveRequestDetails(baseRequest.getDetails(), finalTestCaseValueJson);

                String resolvedUrl = resolvedRequestDetails.get("url").asText();
                JsonNode resolvedHeaders = resolvedRequestDetails.get("headers");
                JsonNode resolvedQueryParams = resolvedRequestDetails.get("queryParams");
                JsonNode resolvedBody = resolvedRequestDetails.get("body");

                // 3. Build and Execute WebClient Request
                // IMPORTANT: Configure base URL for the WebClient. This should come from environment config.
                WebClient client = webClientBuilder.baseUrl("http://localhost:8080").build(); // Replace with actual API base URL from Environment

                return client.method(org.springframework.http.HttpMethod.valueOf(baseRequest.getMethod().name()))
                        .uri(uriBuilder -> {
                            uriBuilder.path(resolvedUrl);
                            if (resolvedQueryParams != null && resolvedQueryParams.isObject()) {
                                resolvedQueryParams.fields().forEachRemaining(entry ->
                                        uriBuilder.queryParam(entry.getKey(), entry.getValue().asText())
                                );
                            }
                            return uriBuilder.build();
                        })
                        .headers(httpHeaders -> {
                            if (resolvedHeaders != null && resolvedHeaders.isObject()) {
                                resolvedHeaders.fields().forEachRemaining(entry ->
                                        httpHeaders.add(entry.getKey(), entry.getValue().asText())
                                );
                            }
                        })
                        .bodyValue(resolvedBody != null && !resolvedBody.isNull() ? resolvedBody : Mono.empty())
                        .exchangeToMono(clientResponse -> {
                            // 4. Capture Response Details
                            long duration = System.currentTimeMillis() - startTimeMillis;
                            Integer statusCode = clientResponse.statusCode().value();
                            HttpHeaders responseHeaders = clientResponse.headers().asHttpHeaders();
                            long responseSize = responseHeaders.getContentLength();
                            if (responseSize == -1) responseSize = 0; // Default if not provided

                            // Capture requestSentDetails (what was actually sent)
                            ObjectNode requestSent = objectMapper.createObjectNode();
                            requestSent.put("method", baseRequest.getMethod().name());
                            requestSent.put("url", clientResponse.request().getURI().toString());
                            requestSent.set("headers", objectMapper.valueToTree(clientResponse.request().getHeaders()));
                            requestSent.set("body", resolvedBody); // Snapshot of what was sent

                            long finalResponseSize = responseSize; // Make effectively final
                            return clientResponse.bodyToMono(String.class) // Read response body
                                    .defaultIfEmpty("") // Handle empty body
                                    .map(body -> {
                                        // 5. Perform Assertions and Determine Status
                                        ExecutionResultStatus finalStatus;
                                        ObjectNode assertionResults = objectMapper.createObjectNode(); // Store assertion results

                                        // Apply the new pass/fail logic based on isExpectedSuccess
                                        boolean isSuccessStatusCode = (statusCode >= 100 && statusCode < 400);

                                        if (savedResult.getIsExpectedSuccess()) { // Use savedResult's isExpectedSuccess
                                            // Expected success (100-399)
                                            if (isSuccessStatusCode) {
                                                finalStatus = ExecutionResultStatus.PASSED;
                                                assertionResults.put("statusCodeAssertion", "PASSED: Expected success, got " + statusCode);
                                            } else {
                                                finalStatus = ExecutionResultStatus.FAILED;
                                                assertionResults.put("statusCodeAssertion", "FAILED: Expected success, got " + statusCode);
                                            }
                                        } else {
                                            // Expected failure (400-599)
                                            if (!isSuccessStatusCode) { // i.e., 4xx or 5xx
                                                finalStatus = ExecutionResultStatus.PASSED; // Passed because it failed as expected
                                                assertionResults.put("statusCodeAssertion", "PASSED: Expected failure, got " + statusCode);
                                            } else { // i.e., 1xx, 2xx, 3xx
                                                finalStatus = ExecutionResultStatus.FAILED; // Failed because it passed, but was expected to fail
                                                assertionResults.put("statusCodeAssertion", "FAILED: Expected failure, got " + statusCode);
                                            }
                                        }

                                        // TODO: Implement more complex body/header assertions based on testCase.value.get("bodyAssertions")
                                        // This would involve JSONPath or similar for parsing 'body' and comparing.
                                        // Example structure for bodyAssertions in TestCase.value:
                                        // "bodyAssertions": [
                                        //   {"path": "$.data.id", "type": "not_null"},
                                        //   {"path": "$.message", "type": "equals", "value": "User created successfully"},
                                        //   {"path": "$.data.email", "type": "regex", "value": "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"}
                                        // ]


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

                                        resultRepository.update(savedResult); // Update the result in DB
                                        log.info("Request execution {} completed with status {}", savedResult.getResultId(), finalStatus);
                                        return savedResult;
                                    });
                        })
                        .onErrorResume(e -> {
                            // 6. Handle Execution Errors (e.g., network issues, timeouts)
                            log.error("Error executing request {}: {}", baseRequest.getId(), e.getMessage(), e);
                            savedResult.setEndTimestamp(LocalDateTime.now());
                            savedResult.setStatus(ExecutionResultStatus.ERROR);
                            savedResult.setResponseBody("Error during execution: " + e.getMessage());
                            savedResult.setAssertionResults(objectMapper.createObjectNode().put("executionError", e.getMessage()));
                            savedResult.setDurationMs((int) (System.currentTimeMillis() - startTimeMillis));
                            resultRepository.update(savedResult); // Update the result in DB
                            return Mono.just(savedResult); // Return the errored result
                        });
            });
        });
    }


    private JsonNode resolveRequestDetails(JsonNode baseRequestDetails, JsonNode testCaseValue) {
        ObjectNode resolvedDetails = objectMapper.createObjectNode();

        if (baseRequestDetails != null && baseRequestDetails.isObject()) {
            resolvedDetails = baseRequestDetails.deepCopy();
        } else {
            resolvedDetails.put("url", "");
            resolvedDetails.putObject("pathVariables");
            resolvedDetails.putObject("queryParams");
            resolvedDetails.putObject("headers");
            resolvedDetails.set("body", objectMapper.nullNode());
            resolvedDetails.put("description", "");
        }

        if (testCaseValue != null && testCaseValue.isObject() && testCaseValue.has("requestOverrides")) {
            JsonNode overrides = testCaseValue.get("requestOverrides");
            if (overrides.isObject()) {
                if (overrides.has("pathVariables") && overrides.get("pathVariables").isObject()) {
                    ((ObjectNode) resolvedDetails.get("pathVariables")).setAll((ObjectNode) overrides.get("pathVariables"));
                }

            }
        }
        return resolvedDetails;
    }

    @Override
    public Mono<ExecutionBatch> getBatchResults(UUID batchId) {
        return Mono.fromCallable(() -> batchRepository.findById(batchId))
                .switchIfEmpty(Mono.error(new NotFoundException("Execution Batch not found with ID: " + batchId)))
                .flatMap(batch -> Mono.fromCallable(() -> {
                    List<ExecutionResult> results = resultRepository.findByBatchId(batch.getBatchId());
                    // You might want to return a DTO that includes both batch and results
                    // For now, just logging and returning the batch.
                    log.debug("Fetched batch {} with {} results.", batchId, results.size());
                    return batch;
                }));
    }

    @Override
    public Mono<List<ExecutionBatch>> getAllBatchesForProject(UUID projectId) {
        return Mono.fromCallable(() -> batchRepository.findByProjectId(projectId));
    }

    @Override
    public Mono<List<ExecutionBatch>> getAllBatches() {
        // This method should ideally be restricted to admins or return only batches related to user's projects
        return Mono.fromCallable(batchRepository::findAll);
    }

    @Override
    public Mono<ExecutionResult> getExecutionResultDetails(UUID resultId) {
        return Mono.fromCallable(() -> resultRepository.findById(resultId))
                .switchIfEmpty(Mono.error(new NotFoundException("Execution Result not found with ID: " + resultId)));
    }
}
