package com.example.Lab8.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public class StableMatchRequest {
    
    @NotEmpty(message = "Students list cannot be empty")
    @Valid
    private List<StudentDTO> students;
    
    @NotEmpty(message = "Courses list cannot be empty")
    @Valid
    private List<CourseDTO> courses;
    
    @Valid
    private Map<String, List<String>> studentPreferences; // studentId -> list of courseIds in preference order
    
    @Valid
    private Map<String, List<String>> coursePreferences; // courseId -> list of studentIds in preference order
    
    public StableMatchRequest() {
    }
    
    public StableMatchRequest(List<StudentDTO> students, List<CourseDTO> courses, 
                             Map<String, List<String>> studentPreferences, 
                             Map<String, List<String>> coursePreferences) {
        this.students = students;
        this.courses = courses;
        this.studentPreferences = studentPreferences;
        this.coursePreferences = coursePreferences;
    }
    
    public List<StudentDTO> getStudents() {
        return students;
    }
    
    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }
    
    public List<CourseDTO> getCourses() {
        return courses;
    }
    
    public void setCourses(List<CourseDTO> courses) {
        this.courses = courses;
    }
    
    public Map<String, List<String>> getStudentPreferences() {
        return studentPreferences;
    }
    
    public void setStudentPreferences(Map<String, List<String>> studentPreferences) {
        this.studentPreferences = studentPreferences;
    }
    
    public Map<String, List<String>> getCoursePreferences() {
        return coursePreferences;
    }
    
    public void setCoursePreferences(Map<String, List<String>> coursePreferences) {
        this.coursePreferences = coursePreferences;
    }
    
    public static class StudentDTO {
        @NotEmpty(message = "Student ID cannot be empty")
        private String id;
        private String name;
        private String code;
        
        public StudentDTO() {
        }
        
        public StudentDTO(String id, String name, String code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
    }
    
    public static class CourseDTO {
        @NotEmpty(message = "Course ID cannot be empty")
        private String id;
        private String name;
        private String code;
        private String abbr;
        private Integer capacity; // maximum number of students that can be assigned
        
        public CourseDTO() {
        }
        
        public CourseDTO(String id, String name, String code, String abbr, Integer capacity) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.abbr = abbr;
            this.capacity = capacity;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getAbbr() {
            return abbr;
        }
        
        public void setAbbr(String abbr) {
            this.abbr = abbr;
        }
        
        public Integer getCapacity() {
            return capacity;
        }
        
        public void setCapacity(Integer capacity) {
            this.capacity = capacity;
        }
    }
}
