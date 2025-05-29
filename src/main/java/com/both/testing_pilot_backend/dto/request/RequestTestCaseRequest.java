// src/main/java/com/both/testing_pilot_backend/dto/request/RequestTestCaseRequest.java
package com.both.testing_pilot_backend.dto.request;

import com.both.testing_pilot_backend.model.enums.ApplicationContextType;
import com.both.testing_pilot_backend.utils.validation.ValidRequestTestCaseLink;
import io.swagger.v3.oas.annotations.media.Schema;
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
@ValidRequestTestCaseLink
@Schema(description = "DTO for creating and updating Request-TestCase links")
public class RequestTestCaseRequest {

    @NotNull(message = "Request ID cannot be null")
    @Schema(description = "ID of the Request to link", example = "123e4567-e89b-12d3-a456-000000000001")
    private UUID requestId;

    @NotNull(message = "Test Case ID cannot be null")
    @Schema(description = "ID of the Test Case to link", example = "f1e2d3c4-b5a6-7890-1234-000000000001")
    private UUID testCaseId;

    @NotNull(message = "Application context cannot be null")
    @Schema(description = "Specifies how the test case's value applies to the request", example = "BODY_FIELD",
            allowableValues = {"BODY_FIELD", "PATH_VARIABLE", "ASSERTION_ONLY"})
    private ApplicationContextType applicationContext;

    @NotNull(message = "isExpectedSuccess cannot be null")
    @Schema(description = "Whether this specific Request-TestCase pairing is expected to result in a successful HTTP status (1xx-3xx)", example = "true")
    private Boolean isExpectedSuccess; // Use Boolean wrapper for @NotNull
}
