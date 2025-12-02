package com.example.Lab8.dto;

import java.util.List;

public class StableMatchResponse {
    
    private List<Assignment> assignments;
    private String algorithm; // e.g., "random", "stable"
    private String status; // "success" or "error"
    private String message; // optional message
    
    public StableMatchResponse() {
    }
    
    public StableMatchResponse(List<Assignment> assignments, String algorithm, String status, String message) {
        this.assignments = assignments;
        this.algorithm = algorithm;
        this.status = status;
        this.message = message;
    }
    
    public List<Assignment> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static class Assignment {
        private String studentId;
        private String studentName;
        private String studentCode;
        private String courseId;
        private String courseName;
        private String courseCode;
        
        public Assignment() {
        }
        
        public Assignment(String studentId, String studentName, String studentCode, 
                         String courseId, String courseName, String courseCode) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentCode = studentCode;
            this.courseId = courseId;
            this.courseName = courseName;
            this.courseCode = courseCode;
        }
        
        public String getStudentId() {
            return studentId;
        }
        
        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }
        
        public String getStudentName() {
            return studentName;
        }
        
        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }
        
        public String getStudentCode() {
            return studentCode;
        }
        
        public void setStudentCode(String studentCode) {
            this.studentCode = studentCode;
        }
        
        public String getCourseId() {
            return courseId;
        }
        
        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }
        
        public String getCourseName() {
            return courseName;
        }
        
        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }
        
        public String getCourseCode() {
            return courseCode;
        }
        
        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }
    }
}
