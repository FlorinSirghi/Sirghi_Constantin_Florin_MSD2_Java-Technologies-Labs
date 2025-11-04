package com.example.Lab4.repository;

import com.example.Lab4.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackRepository extends JpaRepository<Pack, Long> {
    List<Pack> findByYear(Integer year);
}