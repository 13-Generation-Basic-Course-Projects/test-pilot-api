//// src/main/java/com/both/testing_pilot_backend/security/ExecutionSecurity.java
//package com.both.testing_pilot_backend.security;
//
//import com.both.testing_pilot_backend.model.ExecutionBatch;
//import com.both.testing_pilot_backend.model.ExecutionResult;
//import com.both.testing_pilot_backend.security.expression.ProjectSecurity;
//import com.both.testing_pilot_backend.service.ExecutionService;
//import com.both.testing_pilot_backend.exceptions.NotFoundException; // Import NotFoundException
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.nio.file.AccessDeniedException;
//import java.util.UUID;
//
//@Component("executionSecurity")
//@RequiredArgsConstructor
//public class ExecutionSecurity {
//
//    private final ExecutionService executionService; // To fetch result and batch
//    private final ProjectSecurity projectSecurity; // To check project ownership/collaboration
//
//    /**
//     * Checks if the current user is authorized to view a specific execution batch.
//     * Authorization is based on the batch's project ownership/collaboration.
//     * @param batchId The ID of the execution batch.
//     * @return true if authorized, false otherwise.
//     */
//    public boolean isBatchAuthorized(UUID batchId) throws AccessDeniedException {
//        // Use .block() for security checks as they must be synchronous
//        ExecutionBatch batch = executionService.getBatchResults(batchId)
//                .onErrorResume(NotFoundException.class, e -> {
//                    // If batch not found, security should return false,
//                    // and NotFoundException will be handled by GlobalExceptionHandler later.
//                    return null; // Return null Mono to signal not found
//                })
//                .block();
//        if (batch == null) {
//            return false; // Batch not found or error during fetch, deny access
//        }
//        return projectSecurity.isProjectOwnerOrCollaborator(batch.getProjectId());
//    }
//
//
//    /**
//     * Checks if the current user is authorized to view a specific execution result.
//     * Authorization is based on the parent batch's project ownership/collaboration.
//     * @param resultId The ID of the execution result.
//     * @return true if authorized, false otherwise.
//     */
//    public boolean isResultAuthorized(UUID resultId) throws AccessDeniedException {
//        ExecutionResult result = executionService.getExecutionResultDetails(resultId)
//                .onErrorResume(NotFoundException.class, e -> {
//                    return null; // Return null Mono
//                })
//                .block(); // Block to get the result synchronously for security check
//        if (result == null) {
//            return false; // Result not found or error, deny access
//        }
//        // Use isBatchAuthorized to reuse the logic for the parent batch
//        return isBatchAuthorized(result.getBatchId());
//    }
//}
