package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {
    private UUID id;
    private String name;
    private String value;
    private Boolean isPredefined;
    private String dataType;
    private LocalDateTime createdAt;
}
