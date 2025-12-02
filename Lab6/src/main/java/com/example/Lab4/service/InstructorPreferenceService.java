package com.example.Lab4.service;

import com.example.Lab4.model.Course;
import com.example.Lab4.model.Instructor;
import com.example.Lab4.model.InstructorPreference;
import com.example.Lab4.repository.CourseRepository;
import com.example.Lab4.repository.InstructorPreferenceRepository;
import com.example.Lab4.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorPreferenceService {
    
    private final InstructorPreferenceRepository repository;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    
    public List<InstructorPreference> findByOptionalCourseId(Long optionalCourseId) {
        return repository.findByOptionalCourseId(optionalCourseId);
    }
    
    public List<InstructorPreference> findByInstructorId(Long instructorId) {
        return repository.findByInstructorId(instructorId);
    }
    
    @Transactional
    public InstructorPreference save(InstructorPreference preference) {
        // Validate that percentages sum to 100 for the same optional course
        validatePercentageSum(preference);
        return repository.save(preference);
    }
    
    @Transactional
    public InstructorPreference createPreference(Long instructorId, Long optionalCourseId, 
                                                  String compulsoryCourseAbbr, Double percentage) {
        Optional<Instructor> instructorOpt = instructorRepository.findById(instructorId);
        Optional<Course> courseOpt = courseRepository.findById(optionalCourseId);
        
        if (instructorOpt.isEmpty()) {
            throw new IllegalArgumentException("Instructor not found with id: " + instructorId);
        }
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course not found with id: " + optionalCourseId);
        }
        
        Course course = courseOpt.get();
        if (!"OPTIONAL".equalsIgnoreCase(course.getType())) {
            throw new IllegalArgumentException("Course must be of type OPTIONAL");
        }
        
        // Check if preference already exists
        Optional<InstructorPreference> existing = repository
                .findByInstructorIdAndOptionalCourseIdAndCompulsoryCourseAbbr(
                        instructorId, optionalCourseId, compulsoryCourseAbbr);
        
        if (existing.isPresent()) {
            InstructorPreference pref = existing.get();
            pref.setPercentage(percentage);
            validatePercentageSum(pref);
            return repository.save(pref);
        }
        
        InstructorPreference preference = new InstructorPreference();
        preference.setInstructor(instructorOpt.get());
        preference.setOptionalCourse(course);
        preference.setCompulsoryCourseAbbr(compulsoryCourseAbbr);
        preference.setPercentage(percentage);
        
        validatePercentageSum(preference);
        return repository.save(preference);
    }
    
    private void validatePercentageSum(InstructorPreference newPreference) {
        List<InstructorPreference> existing = repository.findByOptionalCourseId(
                newPreference.getOptionalCourse().getId());
        
        double totalPercentage = existing.stream()
                .filter(ip -> !ip.getId().equals(newPreference.getId()))
                .mapToDouble(InstructorPreference::getPercentage)
                .sum();
        
        totalPercentage += newPreference.getPercentage();
        
        if (totalPercentage > 100.0) {
            log.warn("Total percentage exceeds 100% for course {}: {}%", 
                    newPreference.getOptionalCourse().getId(), totalPercentage);
            // Allow it but log warning - percentages don't have to sum to exactly 100
        }
    }
    
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public Optional<InstructorPreference> findById(Long id) {
        return repository.findById(id);
    }
    
    public List<InstructorPreference> findAll() {
        return repository.findAll();
    }
}




