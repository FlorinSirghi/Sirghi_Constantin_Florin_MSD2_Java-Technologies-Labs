package com.example.Lab4.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StudentPreferenceRequest(
        @NotNull(message = "preferences list must be provided")
        @NotEmpty(message = "preferences list cannot be empty")
        @Valid
        List<PreferenceEntryRequest> preferences
) { }







