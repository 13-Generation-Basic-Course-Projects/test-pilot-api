package com.both.testing_pilot_backend.utils.validation;

import com.both.testing_pilot_backend.utils.validation.RequestTestCaseLinkValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RequestTestCaseLinkValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRequestTestCaseLink {
    String message() default "Invalid Request-Test Case link: ensure Request and Test Case exist, and application context is consistent with TestCase.value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
