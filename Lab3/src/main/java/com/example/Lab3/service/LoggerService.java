package com.example.Lab3.service;

import org.springframework.stereotype.Service;

@Service
public class LoggerService {
    
    public void log(String message) {
        System.out.println("[LoggerService] " + message);
    }
    
    public void logInjectionOrder(String injectionType, String beanName) {
        System.out.println("[LoggerService] " + injectionType + " injection completed for: " + beanName);
    }
}
