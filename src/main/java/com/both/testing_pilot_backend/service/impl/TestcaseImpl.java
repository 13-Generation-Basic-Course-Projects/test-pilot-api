package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.repository.TestcaseRepository;
import com.both.testing_pilot_backend.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class TestcaseImpl implements TestcaseService {
    private final TestcaseRepository testcaseRepository;



    @Override
    public TestCase getTestCaseById(UUID id) {
        return testcaseRepository.getTestCaseById(id);
    }
}
