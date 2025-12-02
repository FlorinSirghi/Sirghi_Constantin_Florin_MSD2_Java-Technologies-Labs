package com.example.Lab4.repository;

import com.example.Lab4.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByCourseId(Long courseId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.course.id = :courseId")
    Optional<Grade> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    @Query("SELECT g FROM Grade g JOIN g.student s WHERE s.code = :studentCode")
    List<Grade> findByStudentCode(@Param("studentCode") String studentCode);
    
    @Query("SELECT g FROM Grade g JOIN g.course c WHERE c.code = :courseCode")
    List<Grade> findByCourseCode(@Param("courseCode") String courseCode);
    
    @Query("SELECT g FROM Grade g JOIN g.course c WHERE c.abbr = :courseAbbr AND g.student.id = :studentId")
    Optional<Grade> findByStudentIdAndCourseAbbr(@Param("studentId") Long studentId, @Param("courseAbbr") String courseAbbr);
    
    @Query("SELECT g FROM Grade g JOIN g.course c WHERE c.abbr = :courseAbbr")
    List<Grade> findByCourseAbbr(@Param("courseAbbr") String courseAbbr);
}

