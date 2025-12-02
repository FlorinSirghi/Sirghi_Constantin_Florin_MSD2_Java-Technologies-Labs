package com.example.Lab4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorPreferenceRequest {
    
    @NotNull(message = "Instructor ID is required")
    private Long instructorId;
    
    @NotNull(message = "Optional course ID is required")
    private Long optionalCourseId;
    
    @NotBlank(message = "Compulsory course abbreviation is required")
    private String compulsoryCourseAbbr;
    
    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage must be at most 100")
    private Double percentage;
}




