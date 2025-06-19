package com.both.testing_pilot_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for pagination request")
public class PageRequest {

    @NotNull(message = "Page number cannot be null")
    @Min(value = 0, message = "Page number must be zero or greater")
    @Schema(description = "Page number starting from 0", example = "0")
    private Integer page;

    @NotNull(message = "Page size cannot be null")
    @Min(value = 1, message = "Page size must be at least 1")
    @Schema(description = "Number of items per page", example = "20")
    private Integer size;

    @Schema(description = "Optional last entity ID for cursor-based pagination", example = "123456789", nullable = true)
    private Long lastId;
}
