package com.both.testing_pilot_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variable {
    private UUID variableId;
    private String keyName;
    private String keyValue;
    private boolean enabled;
    private Project project;
}
