package com.example.Lab4.service;

import com.example.Lab4.model.Course;
import com.example.Lab4.model.Grade;
import com.example.Lab4.model.InstructorPreference;
import com.example.Lab4.model.Student;
import com.example.Lab4.repository.CourseRepository;
import com.example.Lab4.repository.GradeRepository;
import com.example.Lab4.repository.InstructorPreferenceRepository;
import com.example.Lab4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentRankingService {
    
    private final InstructorPreferenceRepository instructorPreferenceRepository;
    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public Double calculateWeightedAverage(Long studentId, Long optionalCourseId) {
        List<InstructorPreference> preferences = instructorPreferenceRepository
                .findByOptionalCourseId(optionalCourseId);
        
        if (preferences.isEmpty()) {
            log.debug("No instructor preferences found for optional course {}", optionalCourseId);
            return null;
        }
        
        double totalWeight = 0.0;
        double weightedSum = 0.0;
        
        for (InstructorPreference preference : preferences) {
            String compulsoryAbbr = preference.getCompulsoryCourseAbbr();
            Double percentage = preference.getPercentage();

            Optional<Grade> gradeOpt = gradeRepository.findByStudentIdAndCourseAbbr(studentId, compulsoryAbbr);
            
            if (gradeOpt.isPresent()) {
                Double grade = gradeOpt.get().getGrade();
                double weight = percentage / 100.0;
                weightedSum += grade * weight;
                totalWeight += weight;
            } else {
                log.debug("No grade found for student {} in compulsory course with abbr {}",
                        studentId, compulsoryAbbr);
            }
        }
        
        if (totalWeight == 0.0) {
            log.debug("No valid grades found for student {} to calculate weighted average for course {}", 
                    studentId, optionalCourseId);
            return null;
        }
        
        double weightedAverage = weightedSum / totalWeight;
        log.debug("Weighted average for student {} in optional course {}: {}", 
                studentId, optionalCourseId, weightedAverage);
        return weightedAverage;
    }

    public List<String> orderStudentsByWeightedAverage(Long optionalCourseId) {
        Optional<Course> courseOpt = courseRepository.findById(optionalCourseId);
        if (courseOpt.isEmpty() || !"OPTIONAL".equalsIgnoreCase(courseOpt.get().getType())) {
            log.warn("Course {} is not an optional course", optionalCourseId);
            return Collections.emptyList();
        }

        List<Student> students = studentRepository.findAll();

        List<StudentScore> studentScores = students.stream()
                .map(student -> {
                    Double weightedAvg = calculateWeightedAverage(student.getId(), optionalCourseId);
                    return new StudentScore(student.getId().toString(), 
                                          student.getCode(), 
                                          weightedAvg != null ? weightedAvg : 0.0);
                })
                .sorted((s1, s2) -> Double.compare(s2.getScore(), s1.getScore())) // Descending order
                .collect(Collectors.toList());
        
        log.info("Ordered {} students for optional course {} by weighted average", 
                studentScores.size(), optionalCourseId);
        
        return studentScores.stream()
                .map(StudentScore::getStudentId)
                .collect(Collectors.toList());
    }

    private static class StudentScore {
        private final String studentId;
        private final String studentCode;
        private final Double score;
        
        public StudentScore(String studentId, String studentCode, Double score) {
            this.studentId = studentId;
            this.studentCode = studentCode;
            this.score = score;
        }
        
        public String getStudentId() { return studentId; }
        public String getStudentCode() { return studentCode; }
        public Double getScore() { return score; }
    }
}




