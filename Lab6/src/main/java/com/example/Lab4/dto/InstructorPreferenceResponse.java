package com.example.Lab4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorPreferenceResponse {
    
    private Long id;
    private Long instructorId;
    private String instructorName;
    private Long optionalCourseId;
    private String optionalCourseCode;
    private String optionalCourseName;
    private String compulsoryCourseAbbr;
    private Double percentage;
}




