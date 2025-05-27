package com.both.testing_pilot_backend.utils.validation;

import com.both.testing_pilot_backend.dto.request.RequestTestCaseRequest;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.model.TestCase;
import com.both.testing_pilot_backend.model.RequestTestCase;
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.repository.TestCaseRepository;
import com.both.testing_pilot_backend.repository.RequestTestCaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestTestCaseLinkValidator implements ConstraintValidator<ValidRequestTestCaseLink , RequestTestCaseRequest> {

    private final RequestRepository requestRepository;
    private final TestCaseRepository testCaseRepository;
    private final RequestTestCaseRepository requestTestCaseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void initialize(ValidRequestTestCaseLink constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RequestTestCaseRequest linkRequest, ConstraintValidatorContext context) {
        if (linkRequest.getRequestId() == null || linkRequest.getTestCaseId() == null || linkRequest.getApplicationContext() == null || linkRequest.getIsExpectedSuccess() == null) {
            return false;
        }

        // --- 2. Check if Request and Test Case exist ---
        Request request = requestRepository.findById(linkRequest.getRequestId());
        if (request == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Request with ID '" + linkRequest.getRequestId() + "' not found.")
                    .addPropertyNode("requestId")
                    .addConstraintViolation();
            return false;
        }

        TestCase testCase = testCaseRepository.findById(linkRequest.getTestCaseId());
        if (testCase == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Test Case with ID '" + linkRequest.getTestCaseId() + "' not found.")
                    .addPropertyNode("testCaseId")
                    .addConstraintViolation();
            return false;
        }

        // --- 3. Check Uniqueness (request_id, test_case_id, application_context) ---
        // This is important for updates as well (prevent creating a duplicate during PUT)
        RequestTestCase existingLink = requestTestCaseRepository.findByRequestIdAndTestCaseId(linkRequest.getRequestId(), linkRequest.getTestCaseId());
        if (existingLink != null && linkRequest.getApplicationContext().equals(existingLink.getApplicationContext())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("A link already exists for Request ID '" + linkRequest.getRequestId() +
                            "', Test Case ID '" + linkRequest.getTestCaseId() +
                            "' with application context '" + linkRequest.getApplicationContext() + "'.")
                    .addConstraintViolation();
            return false;
        }


        JsonNode testCaseValueJson = null;
        if (testCase.getValue() != null) {
            try {
                testCaseValueJson = objectMapper.readTree(testCase.getValue());
            } catch (JsonProcessingException e) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Test Case value is not valid JSON for ID '" + testCase.getId() + "'.")
                        .addPropertyNode("testCaseId")
                        .addConstraintViolation();
                return false;
            }
        }

        if (testCaseValueJson == null || !testCaseValueJson.isObject()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Test Case value must be a non-empty JSON object if application context is not ASSERTION_ONLY.")
                        .addPropertyNode("testCaseId")
                        .addConstraintViolation();
                return false;
        }

        if (testCaseValueJson != null && testCaseValueJson.has("requestOverrides") && testCaseValueJson.get("requestOverrides").isObject()) {
            JsonNode overrides = testCaseValueJson.get("requestOverrides");
            switch (linkRequest.getApplicationContext()) {
                case BODY_FIELD:
                    if (!overrides.has("body") || !overrides.get("body").isObject()) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate("Test Case value must contain a 'requestOverrides.body' object for BODY_FIELD context.")
                                .addPropertyNode("testCaseId")
                                .addConstraintViolation();
                        return false;
                    }
                    break;
                case PATH_VARIABLE:
                    if (!overrides.has("pathVariables") || !overrides.get("pathVariables").isObject()) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate("Test Case value must contain a 'requestOverrides.pathVariables' object for PATH_VARIABLE context.")
                                .addPropertyNode("testCaseId")
                                .addConstraintViolation();
                        return false;
                    }
                    break;
            }
        } else  {
                               context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Test Case value must contain 'requestOverrides' for context '" + linkRequest.getApplicationContext() + "'.")
                    .addPropertyNode("testCaseId")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
