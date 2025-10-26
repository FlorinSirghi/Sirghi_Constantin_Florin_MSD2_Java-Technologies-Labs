package com.example.Lab3.demo;

import com.example.Lab3.service.ConfigurationService;
import com.example.Lab3.service.LoggerService;
import com.example.Lab3.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DependencyInjectionDemo {

    private final LoggerService loggerService;

    public DependencyInjectionDemo(LoggerService loggerService, ConfigurationService configurationService) {
        this.loggerService = loggerService;
        loggerService.log("Constructor injection executed");
    }

    @Autowired
    private ValidationService validationService;

    private LoggerService setterLoggerService;
    
    @Autowired
    public void setSetterLoggerService(LoggerService loggerService) {
        if (validationService != null) {
            loggerService.log("Field injection was executed before setter injection");
        }
        this.setterLoggerService = loggerService;
        loggerService.log("Setter injection executed");
    }

    private ValidationService methodValidationService;
    
    @Autowired
    public void injectValidationService(ValidationService validationService) {
        this.methodValidationService = validationService;
        loggerService.log("Method injection executed");
    }

    @PostConstruct
    public void demonstrateInjectionOrder() {
        System.out.println("");
        loggerService.log("All dependency injections completed!");

        loggerService.log("Constructor injection working: " + (loggerService != null));
        loggerService.log("Field injection working: " + (validationService != null));
        loggerService.log("Setter injection working: " + (setterLoggerService != null));
        loggerService.log("Method injection working: " + (methodValidationService != null));
    }
}
