package com.example.Lab4.repository;

import com.example.Lab4.model.StudentPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentPreferenceRepository extends JpaRepository<StudentPreference, Long> {
    List<StudentPreference> findByStudentIdOrderByPriorityAscCourseId(Long studentId);
    void deleteByStudentId(Long studentId);
    boolean existsByStudentId(Long studentId);
    Optional<StudentPreference> findFirstByStudentId(Long studentId);
}



