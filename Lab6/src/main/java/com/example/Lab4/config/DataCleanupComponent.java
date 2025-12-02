package com.example.Lab4.config;

import com.example.Lab4.service.InstructorPreferenceService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataCleanupComponent {
    
    private final InstructorPreferenceService instructorPreferenceService;
    
    @PreDestroy
    public void cleanup() {
        log.info("Application shutting down - cleaning up instructor preferences...");
        try {
            instructorPreferenceService.findAll().forEach(ip -> {
                try {
                    instructorPreferenceService.delete(ip.getId());
                } catch (Exception e) {
                    log.warn("Failed to delete instructor preference {}: {}", ip.getId(), e.getMessage());
                }
            });
            log.info("Instructor preferences cleanup completed.");
        } catch (Exception e) {
            log.error("Error during cleanup: {}", e.getMessage(), e);
        }
    }
}


