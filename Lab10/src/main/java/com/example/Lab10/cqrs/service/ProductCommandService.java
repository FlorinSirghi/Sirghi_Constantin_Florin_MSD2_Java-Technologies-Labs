package com.example.Lab10.cqrs.service;

import com.example.Lab10.cqrs.model.Product;
import com.example.Lab10.cqrs.model.ProductEvent;
import com.example.Lab10.cqrs.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCommandService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public Product createProduct(String name, Double price) {
        Product product = new Product(name, price);
        product = productRepository.save(product);

        ProductEvent event = new ProductEvent("ProductCreated", product.getId(), product);
        eventPublisher.publishEvent(event);
        
        return product;
    }
}

