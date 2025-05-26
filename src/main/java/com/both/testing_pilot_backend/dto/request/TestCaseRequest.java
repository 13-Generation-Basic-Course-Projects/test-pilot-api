package com.both.testing_pilot_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequest {
    private String name;
    private String value;
    private String dataTypeId;
    private String projectId;
}
