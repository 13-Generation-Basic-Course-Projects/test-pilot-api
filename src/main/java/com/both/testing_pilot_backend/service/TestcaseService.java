package com.both.testing_pilot_backend.service;

import com.both.testing_pilot_backend.dto.request.PageRequest;
import com.both.testing_pilot_backend.dto.request.ProjectRequest;
import com.both.testing_pilot_backend.model.Project;
import com.both.testing_pilot_backend.model.TestCase;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

public interface TestcaseService {
     TestCase getTestCaseById(UUID id);


}
