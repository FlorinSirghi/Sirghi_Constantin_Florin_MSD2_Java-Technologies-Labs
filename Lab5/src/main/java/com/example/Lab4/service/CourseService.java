package com.example.Lab4.service;

import com.example.Lab4.model.Course;
import com.example.Lab4.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository repo;
    public CourseService(CourseRepository repo) { this.repo = repo; }
    public Course save(Course c) { return repo.save(c); }
    public Optional<Course> findById(Long id) { return repo.findById(id); }
    public List<Course> findByType(String type) { return repo.findByType(type); }
    public List<Course> findByPackYear(Integer year) { return repo.findByPackYear(year); }
    public int removeInstructorFromCourses(Long instructorId) { return repo.removeInstructorFromCourses(instructorId); }
    public void deleteById(Long id) { repo.deleteById(id); }
    public List<Course> findAll() { return repo.findAll(); }
    public void deleteAll() { repo.deleteAll(); }
}