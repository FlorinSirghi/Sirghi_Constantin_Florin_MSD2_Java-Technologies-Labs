package com.example.Lab3.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    
    public boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
    public String validateAndFormat(String input) {
        if (isValid(input)) {
            return input.trim().toUpperCase();
        }
        return "INVALID";
    }
}
