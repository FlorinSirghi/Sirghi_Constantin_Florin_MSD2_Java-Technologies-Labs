package com.example.Lab10.cqrs.service;

import com.example.Lab10.cqrs.model.ProductReadModel;
import com.example.Lab10.cqrs.repository.ProductReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductQueryService {
    
    @Autowired
    private ProductReadModelRepository readModelRepository;

    public List<ProductReadModel> getAllProducts() {
        return readModelRepository.findAll();
    }
}


