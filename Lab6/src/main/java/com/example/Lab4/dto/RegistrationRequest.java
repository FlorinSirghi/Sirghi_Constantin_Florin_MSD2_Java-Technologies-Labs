package com.example.Lab4.dto;

import com.example.Lab4.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull UserRole role,
        String code,
        Integer year
) {
}




