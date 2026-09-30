package com.recipemanager.common;

/** One entry of the {@code errors} array in a validation {@code ProblemDetail} (TECH_SPEC §4.7). */
public record FieldValidationError(String field, String message) {
}
