package com.example.Lab4.service;

import com.example.Lab4.model.Student;
import com.example.Lab4.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository repo;
    public StudentService(StudentRepository repo) { this.repo = repo; }

    public Student save(Student s) { return repo.save(s); }
    public Optional<Student> findById(Long id) { return repo.findById(id); }
    public List<Student> findByYear(Integer year) { return repo.findByYear(year); }
    public List<Student> findByEmailDomain(String domain) { return repo.findByEmailDomain(domain); }
    public int updateEmail(Long id, String email) { return repo.updateEmailById(id, email); }
    public List<Student> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
    public void deleteById(Long id) { repo.deleteById(id); }
}