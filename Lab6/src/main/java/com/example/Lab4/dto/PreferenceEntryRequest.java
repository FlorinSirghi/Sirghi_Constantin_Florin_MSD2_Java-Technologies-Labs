package com.example.Lab4.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PreferenceEntryRequest(
        @NotNull(message = "courseId is required")
        Long courseId,

        @NotNull(message = "priority is required")
        @Min(value = 1, message = "priority must be >= 1")
        Integer priority,

        @Min(value = 1, message = "tieGroup must be >= 1 when provided")
        Integer tieGroup
) { }



