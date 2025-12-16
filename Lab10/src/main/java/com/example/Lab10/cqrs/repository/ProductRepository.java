package com.example.Lab10.cqrs.repository;

import com.example.Lab10.cqrs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}




