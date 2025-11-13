package com.example.Lab4.service;

import com.example.Lab4.dto.PreferenceEntryRequest;
import com.example.Lab4.exception.PreferenceValidationException;
import com.example.Lab4.model.Course;
import com.example.Lab4.model.Student;
import com.example.Lab4.model.StudentPreference;
import com.example.Lab4.repository.CourseRepository;
import com.example.Lab4.repository.StudentPreferenceRepository;
import com.example.Lab4.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentPreferenceService {

    private final StudentPreferenceRepository preferenceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentPreferenceService(StudentPreferenceRepository preferenceRepository,
                                    StudentRepository studentRepository,
                                    CourseRepository courseRepository) {
        this.preferenceRepository = preferenceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public List<StudentPreference> replacePreferences(Long studentId, List<PreferenceEntryRequest> entries) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new PreferenceValidationException("Student with id %d not found".formatted(studentId)));

        List<Course> coursesForYear = courseRepository.findByPackYear(student.getYear());
        if (coursesForYear.isEmpty()) {
            throw new PreferenceValidationException("No courses configured for year %d".formatted(student.getYear()));
        }

        if (entries == null || entries.isEmpty()) {
            throw new PreferenceValidationException("Preferences must include all courses for year %d".formatted(student.getYear()));
        }

        Map<Long, Course> coursesById = coursesForYear.stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        Set<Long> providedCourseIds = new LinkedHashSet<>();
        for (PreferenceEntryRequest entry : entries) {
            if (!providedCourseIds.add(entry.courseId())) {
                throw new PreferenceValidationException("Duplicate courseId in preferences: " + entry.courseId());
            }
            if (!coursesById.containsKey(entry.courseId())) {
                throw new PreferenceValidationException("Course %d is not available for year %d".formatted(entry.courseId(), student.getYear()));
            }
        }

        if (providedCourseIds.size() != coursesById.size()) {
            throw new PreferenceValidationException("Preferences must cover all %d courses for year %d".formatted(coursesById.size(), student.getYear()));
        }

        preferenceRepository.deleteByStudentId(studentId);

        List<StudentPreference> toSave = entries.stream()
                .sorted(Comparator.comparing(PreferenceEntryRequest::priority).thenComparing(PreferenceEntryRequest::courseId))
                .map(entry -> {
                    StudentPreference pref = new StudentPreference();
                    pref.setStudent(student);
                    pref.setCourse(coursesById.get(entry.courseId()));
                    pref.setPriority(entry.priority());
                    pref.setTieGroup(entry.tieGroup());
                    return pref;
                })
                .toList();

        List<StudentPreference> saved = new ArrayList<>(toSave.size());
        toSave.forEach(pref -> saved.add(preferenceRepository.save(pref)));
        return saved;
    }

    public List<StudentPreference> getPreferences(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new PreferenceValidationException("Student with id %d not found".formatted(studentId));
        }
        List<StudentPreference> preferences = preferenceRepository.findByStudentIdOrderByPriorityAscCourseId(studentId);
        return preferences;
    }

    @Transactional
    public void deletePreferences(Long studentId) {
        if (!preferenceRepository.existsByStudentId(studentId)) {
            throw new PreferenceValidationException("Preferences not found for student %d".formatted(studentId));
        }
        preferenceRepository.deleteByStudentId(studentId);
    }

    public Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new PreferenceValidationException("Student with id %d not found".formatted(studentId)));
    }

    public boolean preferencesExist(Long studentId) {
        return preferenceRepository.existsByStudentId(studentId);
    }
}

