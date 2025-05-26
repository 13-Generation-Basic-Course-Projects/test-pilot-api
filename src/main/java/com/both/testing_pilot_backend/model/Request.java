package com.both.testing_pilot_backend.model;

import com.fasterxml.jackson.databind.JsonNode;
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
public class Request {
    private UUID id;
    private String name;
    private UUID collectionId;
    private HttpMethod method;
    private JsonNode details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
