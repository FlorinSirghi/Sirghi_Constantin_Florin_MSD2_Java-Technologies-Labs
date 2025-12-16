package com.example.Lab10.cqrs.service;

import com.example.Lab10.cqrs.model.Product;
import com.example.Lab10.cqrs.model.ProductReadModel;
import com.example.Lab10.cqrs.repository.ProductReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ProductReadModelUpdateService {
    
    @Autowired
    private ProductReadModelRepository readModelRepository;

    @EventListener
    public void handleProductEvent(com.example.Lab10.cqrs.model.ProductEvent event) {
        if ("ProductCreated".equals(event.getEventType())) {
            Product product = event.getProduct();
            ProductReadModel readModel = new ProductReadModel(
                product.getId().toString(),
                product.getName(),
                product.getPrice()
            );
            readModelRepository.save(readModel);
        }
    }
}

