package com.example.Lab4.service;

import com.example.Lab4.model.Instructor;
import com.example.Lab4.repository.InstructorRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class InstructorService {
    private final InstructorRepository repo;
    public InstructorService(InstructorRepository repo) { this.repo = repo; }
    public Instructor save(Instructor i) { return repo.save(i); }
    public Optional<Instructor> findById(Long id) { return repo.findById(id); }
    public Optional<Instructor> findByEmail(String email) { return repo.findByEmail(email); }
    public List<Instructor> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
}