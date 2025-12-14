package com.example.Lab8.service;

import com.example.Lab8.dto.StableMatchRequest;
import com.example.Lab8.dto.StableMatchResponse;
import com.example.Lab8.model.Assignment;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class StableMatchService {
    
    private static final Logger log = LoggerFactory.getLogger(StableMatchService.class);
    
    private final Map<Long, Assignment> assignments = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Counter stableMatchCounter;
    private final Timer stableMatchTimer;

    public StableMatchService(Counter stableMatchCounter, Timer stableMatchTimer) {
        this.stableMatchCounter = stableMatchCounter;
        this.stableMatchTimer = stableMatchTimer;
    }

    public StableMatchResponse performRandomMatching(StableMatchRequest request) {
        log.info("Starting StableMatch algorithm execution");
        stableMatchCounter.increment();
        
        Timer.Sample sample = Timer.start();
        try {
            StableMatchResponse response = performMatching(request);
            sample.stop(stableMatchTimer);
            return response;
        } catch (Exception e) {
            sample.stop(stableMatchTimer);
            log.error("Error during StableMatch algorithm execution", e);
            throw e;
        }
    }
    
    private StableMatchResponse performMatching(StableMatchRequest request) {
        log.info("Performing random matching for {} students and {} courses", 
                request.getStudents().size(), request.getCourses().size());
        
        List<StableMatchResponse.Assignment> resultAssignments = new ArrayList<>();

        Map<String, StableMatchRequest.StudentDTO> studentMap = request.getStudents().stream()
                .collect(Collectors.toMap(StableMatchRequest.StudentDTO::getId, s -> s));
        
        Map<String, StableMatchRequest.CourseDTO> courseMap = request.getCourses().stream()
                .collect(Collectors.toMap(StableMatchRequest.CourseDTO::getId, c -> c));
        
        // Track course capacities
        Map<String, Integer> courseCapacity = new HashMap<>();
        Map<String, Integer> courseCurrentCount = new HashMap<>();
        
        for (StableMatchRequest.CourseDTO course : request.getCourses()) {
            int capacity = (course.getCapacity() != null && course.getCapacity() > 0) 
                    ? course.getCapacity() : Integer.MAX_VALUE;
            courseCapacity.put(course.getId(), capacity);
            courseCurrentCount.put(course.getId(), 0);
        }

        List<StableMatchRequest.StudentDTO> shuffledStudents = new ArrayList<>(request.getStudents());
        Collections.shuffle(shuffledStudents);

        List<StableMatchRequest.CourseDTO> shuffledCourses = new ArrayList<>(request.getCourses());
        Collections.shuffle(shuffledCourses);
        
        // Random assignment: assign students to courses until courses are full
        for (StableMatchRequest.StudentDTO student : shuffledStudents) {
            boolean assigned = false;
            
            // Try to assign to a random course
            for (StableMatchRequest.CourseDTO course : shuffledCourses) {
                String courseId = course.getId();
                int currentCount = courseCurrentCount.get(courseId);
                int capacity = courseCapacity.get(courseId);
                
                if (currentCount < capacity) {
                    // Assign student to course
                    StableMatchRequest.CourseDTO courseDTO = courseMap.get(courseId);
                    StableMatchRequest.StudentDTO studentDTO = studentMap.get(student.getId());
                    
                    Assignment assignment = new Assignment(
                            student.getId(),
                            studentDTO.getName(),
                            studentDTO.getCode(),
                            courseId,
                            courseDTO.getName(),
                            courseDTO.getCode()
                    );
                    assignment.setId(idGenerator.getAndIncrement());
                    assignments.put(assignment.getId(), assignment);
                    
                    resultAssignments.add(new StableMatchResponse.Assignment(
                            assignment.getStudentId(),
                            assignment.getStudentName(),
                            assignment.getStudentCode(),
                            assignment.getCourseId(),
                            assignment.getCourseName(),
                            assignment.getCourseCode()
                    ));
                    
                    courseCurrentCount.put(courseId, currentCount + 1);
                    assigned = true;
                    break;
                }
            }
            
            if (!assigned) {
                log.warn("Could not assign student {} to any course (all courses full)", student.getId());
            }
        }
        
        log.info("Random matching completed. {} assignments created.", resultAssignments.size());
        
        StableMatchResponse response = new StableMatchResponse();
        response.setAssignments(resultAssignments);
        response.setAlgorithm("random");
        response.setStatus("success");
        response.setMessage("Random matching completed successfully");
        
        log.info("StableMatch algorithm execution completed successfully");
        return response;
    }

    public List<Assignment> getAllAssignments() {
        return new ArrayList<>(assignments.values());
    }

    public List<Assignment> getAssignmentsByStudentId(String studentId) {
        return assignments.values().stream()
                .filter(a -> a.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<Assignment> getAssignmentsByCourseId(String courseId) {
        return assignments.values().stream()
                .filter(a -> a.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    public void clearAllAssignments() {
        assignments.clear();
        idGenerator.set(1);
    }
}
