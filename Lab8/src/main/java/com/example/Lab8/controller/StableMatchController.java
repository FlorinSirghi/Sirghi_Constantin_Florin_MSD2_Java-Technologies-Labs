package com.example.Lab8.controller;

import com.example.Lab8.dto.AssignmentDTO;
import com.example.Lab8.dto.StableMatchRequest;
import com.example.Lab8.dto.StableMatchResponse;
import com.example.Lab8.model.Assignment;
import com.example.Lab8.service.StableMatchService;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stable-match")
public class StableMatchController {
    
    private static final Logger log = LoggerFactory.getLogger(StableMatchController.class);
    private final StableMatchService stableMatchService;
    
    public StableMatchController(StableMatchService stableMatchService) {
        this.stableMatchService = stableMatchService;
    }
    
    /**
     * Main endpoint for stable matching algorithm.
     * Takes a stable matching problem in JSON format and returns the solution.
     */
    @PostMapping("/solve")
    @Retry(name = "stableMatchRetry", fallbackMethod = "solveFallback")
    @TimeLimiter(name = "stableMatchTimeout")
    public CompletableFuture<ResponseEntity<StableMatchResponse>> solve(
            @Valid @RequestBody StableMatchRequest request) {
        log.info("Received stable match request with {} students and {} courses", 
                request.getStudents().size(), request.getCourses().size());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                StableMatchResponse response = stableMatchService.performRandomMatching(request);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error processing stable match request", e);
                StableMatchResponse errorResponse = new StableMatchResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Error processing request: " + e.getMessage());
                errorResponse.setAlgorithm("random");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        });
    }
    
    /**
     * Fallback method for solve endpoint
     */
    public CompletableFuture<ResponseEntity<StableMatchResponse>> solveFallback(
            StableMatchRequest request, Exception e) {
        log.warn("Fallback method invoked due to: {}", e.getMessage());
        StableMatchResponse fallbackResponse = new StableMatchResponse();
        fallbackResponse.setStatus("error");
        fallbackResponse.setMessage("Service temporarily unavailable. Please try again later.");
        fallbackResponse.setAlgorithm("random");
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse));
    }
    
    /**
     * Get all assignments
     */
    @GetMapping("/assignments")
    @Retry(name = "defaultRetry")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        log.info("Retrieving all assignments");
        List<Assignment> assignments = stableMatchService.getAllAssignments();
        List<AssignmentDTO> dtos = assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Get assignments for a specific student
     */
    @GetMapping("/assignments/student/{studentId}")
    @Retry(name = "defaultRetry")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByStudentId(
            @PathVariable String studentId) {
        log.info("Retrieving assignments for student: {}", studentId);
        List<Assignment> assignments = stableMatchService.getAssignmentsByStudentId(studentId);
        List<AssignmentDTO> dtos = assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/assignments/course/{courseId}")
    @Retry(name = "defaultRetry")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByCourseId(
            @PathVariable String courseId) {
        log.info("Retrieving assignments for course: {}", courseId);
        List<Assignment> assignments = stableMatchService.getAssignmentsByCourseId(courseId);
        List<AssignmentDTO> dtos = assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/assignments")
    public ResponseEntity<Void> clearAllAssignments() {
        log.info("Clearing all assignments");
        stableMatchService.clearAllAssignments();
        return ResponseEntity.noContent().build();
    }
    
    private AssignmentDTO convertToDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setStudentId(assignment.getStudentId());
        dto.setStudentName(assignment.getStudentName());
        dto.setStudentCode(assignment.getStudentCode());
        dto.setCourseId(assignment.getCourseId());
        dto.setCourseName(assignment.getCourseName());
        dto.setCourseCode(assignment.getCourseCode());
        dto.setCreatedAt(assignment.getCreatedAt());
        return dto;
    }
}
