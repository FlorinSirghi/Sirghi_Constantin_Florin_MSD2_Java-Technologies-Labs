package com.example.Lab4.controller;

import com.example.Lab4.dto.InstructorPreferenceRequest;
import com.example.Lab4.dto.InstructorPreferenceResponse;
import com.example.Lab4.model.InstructorPreference;
import com.example.Lab4.service.InstructorPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/instructor-preferences")
@RequiredArgsConstructor
@Slf4j
public class InstructorPreferenceController {
    
    private final InstructorPreferenceService preferenceService;
    
    @PostMapping
    @Operation(summary = "Create instructor preference", 
               description = "Create a preference for an optional course indicating importance of a compulsory course")
    @ApiResponse(responseCode = "201", description = "Preference created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<InstructorPreferenceResponse> createPreference(
            @Valid @RequestBody InstructorPreferenceRequest request) {
        log.info("Creating instructor preference: instructorId={}, optionalCourseId={}, compulsoryAbbr={}, percentage={}",
                request.getInstructorId(), request.getOptionalCourseId(), 
                request.getCompulsoryCourseAbbr(), request.getPercentage());
        
        InstructorPreference preference = preferenceService.createPreference(
                request.getInstructorId(),
                request.getOptionalCourseId(),
                request.getCompulsoryCourseAbbr(),
                request.getPercentage()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(preference));
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get preferences for an optional course",
               description = "Returns all instructor preferences for a specific optional course")
    public ResponseEntity<List<InstructorPreferenceResponse>> getPreferencesByCourse(
            @PathVariable Long courseId) {
        log.info("Retrieving preferences for course: {}", courseId);
        List<InstructorPreference> preferences = preferenceService.findByOptionalCourseId(courseId);
        List<InstructorPreferenceResponse> responses = preferences.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "Get preferences for an instructor",
               description = "Returns all preferences set by a specific instructor")
    public ResponseEntity<List<InstructorPreferenceResponse>> getPreferencesByInstructor(
            @PathVariable Long instructorId) {
        log.info("Retrieving preferences for instructor: {}", instructorId);
        List<InstructorPreference> preferences = preferenceService.findByInstructorId(instructorId);
        List<InstructorPreferenceResponse> responses = preferences.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping
    @Operation(summary = "Get all instructor preferences",
               description = "Returns all instructor preferences (admin only)")
    public ResponseEntity<List<InstructorPreferenceResponse>> getAllPreferences() {
        log.info("Retrieving all instructor preferences");
        List<InstructorPreference> preferences = preferenceService.findAll();
        List<InstructorPreferenceResponse> responses = preferences.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete instructor preference",
               description = "Delete a specific instructor preference by ID")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        log.info("Deleting instructor preference: {}", id);
        preferenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    private InstructorPreferenceResponse toResponse(InstructorPreference preference) {
        InstructorPreferenceResponse response = new InstructorPreferenceResponse();
        response.setId(preference.getId());
        response.setInstructorId(preference.getInstructor().getId());
        response.setInstructorName(preference.getInstructor().getFullName());
        response.setOptionalCourseId(preference.getOptionalCourse().getId());
        response.setOptionalCourseCode(preference.getOptionalCourse().getCode());
        response.setOptionalCourseName(preference.getOptionalCourse().getName());
        response.setCompulsoryCourseAbbr(preference.getCompulsoryCourseAbbr());
        response.setPercentage(preference.getPercentage());
        return response;
    }
}




