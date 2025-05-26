package com.both.testing_pilot_backend.utils.validation;// src/main/java/com/both/testing_pilot_backend/validation/TestCaseConditionValidator.java

import com.both.testing_pilot_backend.dto.request.TestCaseRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class TestCaseConditionValidator implements ConstraintValidator<ValidTestCaseCondition, TestCaseRequest> {

    @Override
    public void initialize(ValidTestCaseCondition constraintAnnotation) {
    }

    @Override
    public boolean isValid(TestCaseRequest request, ConstraintValidatorContext context) {
        return true;
    }
}
