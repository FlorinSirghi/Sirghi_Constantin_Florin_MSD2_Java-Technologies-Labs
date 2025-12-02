package com.example.Lab4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTOs for integration with StableMatch service
 */
public class StableMatchIntegrationDTO {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StableMatchRequest {
        private List<StudentDTO> students;
        private List<CourseDTO> courses;
        private Map<String, List<String>> studentPreferences;
        private Map<String, List<String>> coursePreferences;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentDTO {
        private String id;
        private String name;
        private String code;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseDTO {
        private String id;
        private String name;
        private String code;
        private String abbr;
        private Integer capacity;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StableMatchResponse {
        private List<Assignment> assignments;
        private String algorithm;
        private String status;
        private String message;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assignment {
        private String studentId;
        private String studentName;
        private String studentCode;
        private String courseId;
        private String courseName;
        private String courseCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchResult {
        private Long packId;
        private String packName;
        private Boolean success;
        private String message;
        private Integer assignmentsCount;
        private List<Assignment> assignments;
    }
}




