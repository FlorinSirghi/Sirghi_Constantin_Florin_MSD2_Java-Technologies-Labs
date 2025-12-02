package com.example.QuickGrade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeEvent {
    private String studentCode;
    private String courseCode;
    private Double grade;
}









