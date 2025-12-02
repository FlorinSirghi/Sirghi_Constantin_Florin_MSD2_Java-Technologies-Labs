package com.example.Lab4.service;

import com.example.Lab4.dto.StableMatchIntegrationDTO;
import com.example.Lab4.model.Course;
import com.example.Lab4.model.Pack;
import com.example.Lab4.model.Student;
import com.example.Lab4.model.StudentPreference;
import com.example.Lab4.repository.CourseRepository;
import com.example.Lab4.repository.PackRepository;
import com.example.Lab4.repository.StudentPreferenceRepository;
import com.example.Lab4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service for integrating with the StableMatch service to perform matching
 * for each pack of optional courses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StableMatchIntegrationService {
    
    private final PackRepository packRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentPreferenceRepository studentPreferenceRepository;
    private final StudentRankingService studentRankingService;
    
    @Value("${stablematch.service.url:http://localhost:8081}")
    private String stableMatchServiceUrl;
    
    private WebClient webClient;
    
    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(stableMatchServiceUrl)
                    .build();
        }
        return webClient;
    }
    
    /**
     * Processes matching for all packs of optional courses by calling StableMatch service.
     * 
     * @return Map of pack ID to matching results
     */
    @Retry(name = "stableMatchRetry", fallbackMethod = "matchForPackFallback")
    @TimeLimiter(name = "stableMatchTimeout")
    public CompletableFuture<Map<Long, StableMatchIntegrationDTO.MatchResult>> matchForAllPacks() {
        log.info("Starting matching process for all packs");
        List<Pack> packs = packRepository.findAll();
        Map<Long, StableMatchIntegrationDTO.MatchResult> results = new HashMap<>();
        
        for (Pack pack : packs) {
            try {
                CompletableFuture<StableMatchIntegrationDTO.MatchResult> future = matchForPack(pack.getId());
                StableMatchIntegrationDTO.MatchResult result = future.get();
                results.put(pack.getId(), result);
            } catch (Exception e) {
                log.error("Error matching for pack {}: {}", pack.getId(), e.getMessage(), e);
                results.put(pack.getId(), createErrorResult(e.getMessage()));
            }
        }
        
        return CompletableFuture.completedFuture(results);
    }
    
    /**
     * Processes matching for a specific pack of optional courses.
     * 
     * @param packId The pack ID
     * @return Matching result
     */
    @Retry(name = "stableMatchRetry", fallbackMethod = "matchForPackFallback")
    @TimeLimiter(name = "stableMatchTimeout")
    public CompletableFuture<StableMatchIntegrationDTO.MatchResult> matchForPack(Long packId) {
        log.info("Processing matching for pack: {}", packId);
        
        Optional<Pack> packOpt = packRepository.findById(packId);
        if (packOpt.isEmpty()) {
            log.error("Pack not found: {}", packId);
            return CompletableFuture.completedFuture(createErrorResult("Pack not found"));
        }
        
        Pack pack = packOpt.get();
        
        // Get all optional courses in this pack
        List<Course> optionalCourses = courseRepository.findByType("OPTIONAL").stream()
                .filter(course -> course.getPack() != null && course.getPack().getId().equals(packId))
                .collect(Collectors.toList());
        
        if (optionalCourses.isEmpty()) {
            log.warn("No optional courses found in pack: {}", packId);
            return CompletableFuture.completedFuture(createErrorResult("No optional courses in pack"));
        }
        
        // Get all students
        List<Student> students = studentRepository.findAll();
        
        // Build the StableMatch request
        StableMatchIntegrationDTO.StableMatchRequest request = buildStableMatchRequest(students, optionalCourses, packId);
        
        // Call StableMatch service
        return callStableMatchService(request)
                .timeout(Duration.ofSeconds(10))
                .map(response -> {
                    StableMatchIntegrationDTO.MatchResult result = new StableMatchIntegrationDTO.MatchResult();
                    result.setPackId(packId);
                    result.setPackName(pack.getName());
                    result.setSuccess(true);
                    result.setAssignmentsCount(response.getAssignments() != null ? response.getAssignments().size() : 0);
                    result.setMessage("Matching completed successfully");
                    result.setAssignments(response.getAssignments());
                    return result;
                })
                .onErrorResume(e -> {
                    log.error("Error calling StableMatch service for pack {}: {}", packId, e.getMessage(), e);
                    return Mono.just(createErrorResult("Error calling StableMatch service: " + e.getMessage()));
                })
                .toFuture();
    }
    
    /**
     * Fallback method for matchForPack
     */
    public CompletableFuture<StableMatchIntegrationDTO.MatchResult> matchForPackFallback(Long packId, Exception e) {
        log.warn("Fallback method invoked for pack {} due to: {}", packId, e.getMessage());
        return CompletableFuture.completedFuture(createErrorResult("Service temporarily unavailable: " + e.getMessage()));
    }
    
    private StableMatchIntegrationDTO.StableMatchRequest buildStableMatchRequest(
            List<Student> students, List<Course> optionalCourses, Long packId) {
        
        StableMatchIntegrationDTO.StableMatchRequest request = new StableMatchIntegrationDTO.StableMatchRequest();
        
        // Build student DTOs
        List<StableMatchIntegrationDTO.StudentDTO> studentDTOs = students.stream()
                .map(student -> {
                    StableMatchIntegrationDTO.StudentDTO dto = new StableMatchIntegrationDTO.StudentDTO();
                    dto.setId(student.getId().toString());
                    dto.setName(student.getFullName());
                    dto.setCode(student.getCode());
                    return dto;
                })
                .collect(Collectors.toList());
        
        // Build course DTOs with capacities
        List<StableMatchIntegrationDTO.CourseDTO> courseDTOs = optionalCourses.stream()
                .map(course -> {
                    StableMatchIntegrationDTO.CourseDTO dto = new StableMatchIntegrationDTO.CourseDTO();
                    dto.setId(course.getId().toString());
                    dto.setName(course.getName());
                    dto.setCode(course.getCode());
                    dto.setAbbr(course.getAbbr());
                    // Use groupCount as capacity, default to 30 if not set
                    dto.setCapacity(course.getGroupCount() != null && course.getGroupCount() > 0 
                            ? course.getGroupCount() * 30 : 30);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // Build student preferences (from student_preferences table)
        Map<String, List<String>> studentPreferences = new HashMap<>();
        for (Student student : students) {
            List<StudentPreference> preferences = studentPreferenceRepository.findByStudentIdOrderByPriorityAscCourseId(student.getId())
                    .stream()
                    .filter(pref -> optionalCourses.stream()
                            .anyMatch(course -> course.getId().equals(pref.getCourse().getId())))
                    .sorted(Comparator.comparing(StudentPreference::getPriority))
                    .collect(Collectors.toList());
            
            List<String> courseIds = preferences.stream()
                    .map(pref -> pref.getCourse().getId().toString())
                    .collect(Collectors.toList());
            
            if (!courseIds.isEmpty()) {
                studentPreferences.put(student.getId().toString(), courseIds);
            }
        }
        
        // Build course preferences based on weighted averages (instructor preferences)
        Map<String, List<String>> coursePreferences = new HashMap<>();
        for (Course course : optionalCourses) {
            List<String> orderedStudentIds = studentRankingService.orderStudentsByWeightedAverage(course.getId());
            if (!orderedStudentIds.isEmpty()) {
                coursePreferences.put(course.getId().toString(), orderedStudentIds);
            }
        }
        
        request.setStudents(studentDTOs);
        request.setCourses(courseDTOs);
        request.setStudentPreferences(studentPreferences);
        request.setCoursePreferences(coursePreferences);
        
        return request;
    }
    
    private Mono<StableMatchIntegrationDTO.StableMatchResponse> callStableMatchService(
            StableMatchIntegrationDTO.StableMatchRequest request) {
        
        log.info("Calling StableMatch service at: {}/api/stable-match/solve", stableMatchServiceUrl);
        
        return getWebClient().post()
                .uri("/api/stable-match/solve")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(StableMatchIntegrationDTO.StableMatchResponse.class)
                .doOnSuccess(response -> log.info("StableMatch service returned {} assignments", 
                        response.getAssignments() != null ? response.getAssignments().size() : 0))
                .doOnError(error -> log.error("Error calling StableMatch service", error));
    }
    
    private StableMatchIntegrationDTO.MatchResult createErrorResult(String message) {
        StableMatchIntegrationDTO.MatchResult result = new StableMatchIntegrationDTO.MatchResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setAssignmentsCount(0);
        return result;
    }
}

