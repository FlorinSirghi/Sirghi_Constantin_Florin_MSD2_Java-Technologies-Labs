package com.example.Lab4.dto;

import java.util.Set;

public record AuthResponse(String token, String fullName, Set<String> roles) {
}




