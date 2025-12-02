package com.example.Lab8.model;

import java.time.LocalDateTime;

public class Assignment {
    private Long id;
    private String studentId;
    private String studentName;
    private String studentCode;
    private String courseId;
    private String courseName;
    private String courseCode;
    private LocalDateTime createdAt;
    
    public Assignment() {
    }
    
    public Assignment(Long id, String studentId, String studentName, String studentCode, 
                     String courseId, String courseName, String courseCode, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.createdAt = createdAt;
    }
    
    public Assignment(String studentId, String studentName, String studentCode, 
                     String courseId, String courseName, String courseCode) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
