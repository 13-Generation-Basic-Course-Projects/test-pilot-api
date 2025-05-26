package com.both.testing_pilot_backend.utils.validation;// src/main/java/com/both/testing_pilot_backend/validation/ValidTestCaseCondition.java

import com.both.testing_pilot_backend.utils.validation.TestCaseConditionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TestCaseConditionValidator.class)
@Target({ElementType.TYPE}) // Apply to the class level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTestCaseCondition {
    String message() default "Invalid test case: 'projectId' must be null if 'isPredefined' is true, and not null if 'isPredefined' is false.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
