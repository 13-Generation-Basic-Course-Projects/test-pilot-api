package com.both.testing_pilot_backend.utils.validation;// src/main/java/com/both/testing_pilot_backend/validation/ValidRequestDetailsStructure.java

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RequestDetailsStructureValidator.class) // Link to the validator
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Can be applied to fields or method parameters
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface ValidRequestDetailsStructure {

    String message() default "The 'details' field must contain 'url', 'pathVariables', 'queryParams', 'headers', 'body', and 'description' keys.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
