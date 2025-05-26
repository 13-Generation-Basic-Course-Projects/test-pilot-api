package com.both.testing_pilot_backend.utils.validation;// src/main/java/com/both/testing_pilot_backend/validation/TestCaseConditionValidator.java

import com.both.testing_pilot_backend.dto.request.TestCaseRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class TestCaseConditionValidator implements ConstraintValidator<com.both.testing_pilot_backend.validation.ValidTestCaseCondition, TestCaseRequest> {

    @Override
    public void initialize(com.both.testing_pilot_backend.validation.ValidTestCaseCondition constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(TestCaseRequest request, ConstraintValidatorContext context) {
        boolean isPredefined = request.isPredefined();
        UUID projectId = request.getProjectId();

        if (isPredefined) {
            // If isPredefined is true, projectId must be null
            if (projectId != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("For predefined test cases, 'projectId' must be null.")
                        .addPropertyNode("projectId")
                        .addConstraintViolation();
                return false;
            }
        } else {
            // If isPredefined is false, projectId must NOT be null
            if (projectId == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("For custom test cases, 'projectId' cannot be null.")
                        .addPropertyNode("projectId")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
