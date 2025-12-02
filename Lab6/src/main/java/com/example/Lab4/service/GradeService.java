package com.example.Lab4.service;

import com.example.Lab4.model.Course;
import com.example.Lab4.model.Grade;
import com.example.Lab4.model.Student;
import com.example.Lab4.repository.CourseRepository;
import com.example.Lab4.repository.GradeRepository;
import com.example.Lab4.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public Grade saveGrade(String studentCode, String courseCode, Double gradeValue) {
        Optional<Student> studentOpt = studentRepository.findByCode(studentCode);
        Optional<Course> courseOpt = courseRepository.findByCode(courseCode);

        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student with code " + studentCode + " not found");
        }

        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Course with code " + courseCode + " not found");
        }

        Student student = studentOpt.get();
        Course course = courseOpt.get();

        // Check if course is compulsory
        if (!"COMPULSORY".equalsIgnoreCase(course.getType())) {
            logger.info("Skipping grade for non-compulsory course: studentCode={}, courseCode={}, courseType={}", 
                studentCode, courseCode, course.getType());
            return null;
        }

        // Check if grade already exists
        Optional<Grade> existingGrade = gradeRepository.findByStudentIdAndCourseId(student.getId(), course.getId());
        
        if (existingGrade.isPresent()) {
            Grade grade = existingGrade.get();
            grade.setGrade(gradeValue);
            logger.info("Updated grade: studentCode={}, courseCode={}, grade={}", studentCode, courseCode, gradeValue);
            return gradeRepository.save(grade);
        } else {
            Grade grade = new Grade();
            grade.setStudent(student);
            grade.setCourse(course);
            grade.setGrade(gradeValue);
            logger.info("Saved new grade: studentCode={}, courseCode={}, grade={}", studentCode, courseCode, gradeValue);
            return gradeRepository.save(grade);
        }
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public List<Grade> getGradesByStudentCode(String studentCode) {
        return gradeRepository.findByStudentCode(studentCode);
    }

    public List<Grade> getGradesByCourseCode(String courseCode) {
        return gradeRepository.findByCourseCode(courseCode);
    }

    @Transactional
    public void loadGradesFromCsv(List<String[]> csvData) {
        int saved = 0;
        int skipped = 0;
        for (String[] row : csvData) {
            if (row.length < 3) {
                logger.warn("Invalid CSV row, skipping: {}", String.join(",", row));
                skipped++;
                continue;
            }
            try {
                String studentCode = row[0].trim();
                String courseCode = row[1].trim();
                Double grade = Double.parseDouble(row[2].trim());
                
                Grade savedGrade = saveGrade(studentCode, courseCode, grade);
                if (savedGrade != null) {
                    saved++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                logger.error("Error processing CSV row: {}", String.join(",", row), e);
                skipped++;
            }
        }
        logger.info("CSV import completed: {} saved, {} skipped", saved, skipped);
    }
}









