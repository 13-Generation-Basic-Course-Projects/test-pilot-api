package com.both.testing_pilot_backend.utils.validation;// src/main/java/com/both/testing_pilot_backend/validation/RequestDetailsStructureValidator.java

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class RequestDetailsStructureValidator implements ConstraintValidator<ValidRequestDetailsStructure, JsonNode> {

    // Define the required keys
    private static final List<String> REQUIRED_KEYS = Arrays.asList(
            "url",
            "pathVariables",
            "queryParams",
            "headers",
            "body",
            "description"
    );

    @Override
    public void initialize(ValidRequestDetailsStructure constraintAnnotation) {
        // Initialization logic if needed (e.g., getting parameters from the annotation)
    }

    @Override
    public boolean isValid(JsonNode details, ConstraintValidatorContext context) {
        // If details is null, the @NotNull annotation should handle it.
        // We only validate the structure if details is provided.
        if (details == null || details.isNull()) {
            return true; // @NotNull handles the null case. If it's optional, this should be true.
            // Since @NotNull is also present, this will only be called for non-null JsonNode.
        }

        // Ensure it's an object node
        if (!details.isObject()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The 'details' field must be a JSON object.")
                    .addConstraintViolation();
            return false;
        }

        ObjectNode detailsObject = (ObjectNode) details;

        // Check for the presence of all required keys
        for (String key : REQUIRED_KEYS) {
            if (!detailsObject.has(key)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("The 'details' field is missing the required key: '" + key + "'")
                        .addConstraintViolation();
                return false;
            }
        }

        // Optional: Further validation for specific key types (e.g., pathVariables, queryParams, headers must be objects)
        if (detailsObject.has("pathVariables") && !detailsObject.get("pathVariables").isObject()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The 'pathVariables' key in 'details' must be a JSON object.")
                    .addConstraintViolation();
            return false;
        }
        if (detailsObject.has("queryParams") && !detailsObject.get("queryParams").isObject()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The 'queryParams' key in 'details' must be a JSON object.")
                    .addConstraintViolation();
            return false;
        }
        if (detailsObject.has("headers") && !detailsObject.get("headers").isObject()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The 'headers' key in 'details' must be a JSON object.")
                    .addConstraintViolation();
            return false;
        }
        // 'body' can be null, object, or string, so just checking 'has' is enough for its presence.

        return true;
    }
}
