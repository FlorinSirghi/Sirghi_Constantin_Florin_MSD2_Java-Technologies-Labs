package com.example.Lab4.repository;

import com.example.Lab4.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByYear(Integer year);

    @Query("SELECT s FROM Student s WHERE s.email LIKE %:domain")
    List<Student> findByEmailDomain(String domain);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.email = :email WHERE s.id = :id")
    int updateEmailById(Long id, String email);

    Optional<Student> findByCode(String code);
}