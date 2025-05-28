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
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class RequestTestCaseLinkValidator implements ConstraintValidator<ValidRequestTestCaseLink, RequestTestCaseRequest> {

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
            return false; // Let @NotNull handle the specific message
        }

        // --- 2. Check if Request and Test Case exist ---
        Request request = requestRepository.findById(linkRequest.getRequestId());
        if (request == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Request with ID '" + linkRequest.getRequestId() + "' not found.").addPropertyNode(
                    "requestId").addConstraintViolation();
            return false;
        }

        TestCase testCase = testCaseRepository.findById(linkRequest.getTestCaseId());
        if (testCase == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Test Case with ID '" + linkRequest.getTestCaseId() + "' not found.").addPropertyNode(
                    "testCaseId").addConstraintViolation();
            return false;
        }

        // --- 3. Check Uniqueness (request_id, test_case_id, application_context, target_field_path) ---
        RequestTestCase existingLink = requestTestCaseRepository.findByRequestIdAndTestCaseIdAndApplicationContextAndTargetFieldPath(
                linkRequest.getRequestId(),
                linkRequest.getTestCaseId(),
                linkRequest.getApplicationContext(),
                linkRequest.getTargetFieldPath());

        if (existingLink != null && (linkRequest.getRequestId() == null || !existingLink.getId().equals(linkRequest.getRequestId()))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("A link already exists for Request ID '" + linkRequest.getRequestId() + "', Test Case ID '" + linkRequest.getTestCaseId() + "', application context '" + linkRequest.getApplicationContext() + "' and target field path '" + (linkRequest.getTargetFieldPath() != null ? linkRequest.getTargetFieldPath() : "null") + "'.").addConstraintViolation();
            return false;
        }

        // --- 4. Validate targetFieldPath based on applicationContext ---
        switch (linkRequest.getApplicationContext()) {
            case BODY_FIELD:
            case QUERY_PARAM:
            case PATH_VARIABLE:
                if (!StringUtils.hasText(linkRequest.getTargetFieldPath())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("For application context '" + linkRequest.getApplicationContext() + "', 'targetFieldPath' must be provided.").addPropertyNode(
                            "targetFieldPath").addConstraintViolation();
                    return false;
                }
                break;
        }
        return true;
    }
}