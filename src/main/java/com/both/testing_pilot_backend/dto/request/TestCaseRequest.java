// src/main/java/com/both/testing_pilot_backend/dto/request/TestCaseRequest.java
package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.utils.validation.ValidTestCaseCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTestCaseCondition
@Schema(description = "DTO for creating and updating test cases")
public class TestCaseRequest {
    // projectId is conditionally validated by @ValidTestCaseCondition
    @Schema(description = "ID of the project this test case belongs to. Must be null if isPredefined is true, and not null if isPredefined is false.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    private UUID projectId;

    @NotNull(message = "Data type ID cannot be null")
    @Schema(description = "ID of the data type associated with this test case", example = "f1e2d3c4-b5a6-7890-1234-567890abcdef")
    private UUID dataTypeId;

    @NotBlank(message = "Test case name cannot be blank")
    @Schema(description = "Name of the test case", example = "Valid Email Format")
    private String name;

    @Schema(description = "The value associated with the test case (e.g., a sample email string, a number)", example = "test@example.com", nullable = true)
    private String value;
}
