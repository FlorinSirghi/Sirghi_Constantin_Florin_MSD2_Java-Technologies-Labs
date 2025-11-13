package com.example.Lab4.service;

import com.example.Lab4.model.Instructor;
import com.example.Lab4.model.UserRole;
import com.example.Lab4.repository.InstructorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class InstructorService {
    private final InstructorRepository repo;
    private final PasswordEncoder passwordEncoder;

    public InstructorService(InstructorRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }
    public Instructor save(Instructor i) {
        if (i.getRoles() == null || i.getRoles().isEmpty()) {
            i.addRole(UserRole.INSTRUCTOR);
        }
        if (i.getPassword() != null && !i.getPassword().startsWith("$2")) {
            i.setPassword(passwordEncoder.encode(i.getPassword()));
        } else if (i.getPassword() == null && i.getId() != null) {
            repo.findById(i.getId()).ifPresent(existing -> i.setPassword(existing.getPassword()));
        }
        return repo.save(i);
    }
    public Optional<Instructor> findById(Long id) { return repo.findById(id); }
    public Optional<Instructor> findByEmail(String email) { return repo.findByEmail(email); }
    public List<Instructor> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
}