package com.example.Lab3.service;

import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    
    public String getAppName() {
        return "Spring Boot Dependency Injection Demo";
    }
    
    public String getVersion() {
        return "1.0.0";
    }
}
