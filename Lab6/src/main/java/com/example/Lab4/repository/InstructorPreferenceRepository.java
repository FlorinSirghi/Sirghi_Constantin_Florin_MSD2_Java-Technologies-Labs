package com.example.Lab4.repository;

import com.example.Lab4.model.InstructorPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstructorPreferenceRepository extends JpaRepository<InstructorPreference, Long> {
    
    List<InstructorPreference> findByOptionalCourseId(Long optionalCourseId);
    
    List<InstructorPreference> findByInstructorId(Long instructorId);
    
    @Query("SELECT ip FROM InstructorPreference ip WHERE ip.optionalCourse.id = :courseId")
    List<InstructorPreference> findByOptionalCourse(@Param("courseId") Long courseId);
    
    Optional<InstructorPreference> findByInstructorIdAndOptionalCourseIdAndCompulsoryCourseAbbr(
            Long instructorId, Long optionalCourseId, String compulsoryCourseAbbr);
    
    @Query("SELECT ip FROM InstructorPreference ip WHERE ip.optionalCourse.id IN :courseIds")
    List<InstructorPreference> findByOptionalCourseIds(@Param("courseIds") List<Long> courseIds);
}




