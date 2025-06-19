package com.both.testing_pilot_backend.model;

import com.both.testing_pilot_backend.model.enums.ApplicationContextType;
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
public class RequestTestCase {
    private UUID id;
    private UUID requestId;
    private UUID testCaseId;
    private ApplicationContextType applicationContext;
    private boolean isExpectedSuccess;
    private String targetFieldPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Request request;
    private TestCase testCase;
}
