package com.example.Lab4.repository;

import com.example.Lab4.model.Course;
import com.example.Lab4.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(String type);

    @Query("SELECT c FROM Course c WHERE c.pack.year = :year")
    List<Course> findByPackYear(Integer year);

    @Modifying
    @Transactional
    @Query("UPDATE Course c SET c.instructor = null WHERE c.instructor.id = :instructorId")
    int removeInstructorFromCourses(Long instructorId);

    java.util.Optional<Course> findByCode(String code);
}