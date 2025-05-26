package com.both.testing_pilot_backend.model;

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
public class TestCase {
    private UUID id;
    private UUID projectId;
    private UUID dataTypeId;
    private String name;
    private String value;
    private boolean isPredefined;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional: To include DataType details if needed in responses
    private DataType dataType;
}
