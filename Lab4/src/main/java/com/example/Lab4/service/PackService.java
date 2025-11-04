package com.example.Lab4.service;

import com.example.Lab4.model.Pack;
import com.example.Lab4.repository.PackRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PackService {
    private final PackRepository repo;
    public PackService(PackRepository repo) { this.repo = repo; }
    public Pack save(Pack p) { return repo.save(p); }
    public Optional<Pack> findById(Long id) { return repo.findById(id); }
    public List<Pack> findByYear(Integer year) { return repo.findByYear(year); }
    public List<Pack> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
}