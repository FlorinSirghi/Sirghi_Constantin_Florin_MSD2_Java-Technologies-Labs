package com.example.Lab4.service;

import com.example.Lab4.model.Student;
import com.example.Lab4.model.UserRole;
import com.example.Lab4.repository.StudentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository repo;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public Student save(Student s) {
        if (s.getRoles() == null || s.getRoles().isEmpty()) {
            s.addRole(UserRole.STUDENT);
        }
        if (s.getPassword() != null && !s.getPassword().startsWith("$2")) {
            s.setPassword(passwordEncoder.encode(s.getPassword()));
        } else if (s.getPassword() == null && s.getId() != null) {
            repo.findById(s.getId()).ifPresent(existing -> s.setPassword(existing.getPassword()));
        }
        return repo.save(s);
    }
    public Optional<Student> findById(Long id) { return repo.findById(id); }
    public List<Student> findByYear(Integer year) { return repo.findByYear(year); }
    public List<Student> findByEmailDomain(String domain) { return repo.findByEmailDomain(domain); }
    public int updateEmail(Long id, String email) { return repo.updateEmailById(id, email); }
    public List<Student> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) { repo.deleteById(id); }
}