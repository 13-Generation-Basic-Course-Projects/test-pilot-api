package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.model.TestCase;

import java.util.UUID;

public interface TestcaseService {
    public TestCase getTestCaseById(UUID id);
}
