package com.example.Lab10.cqrs.controller;

import com.example.Lab10.cqrs.model.Product;
import com.example.Lab10.cqrs.model.ProductReadModel;
import com.example.Lab10.cqrs.service.ProductCommandService;
import com.example.Lab10.cqrs.service.ProductQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cqrs/products")
public class ProductController {
    
    @Autowired
    private ProductCommandService commandService;
    
    @Autowired
    private ProductQueryService queryService;

    @PostMapping
    public Product createProduct(@RequestBody CreateProductRequest request) {
        return commandService.createProduct(request.getName(), request.getPrice());
    }

    @GetMapping
    public List<ProductReadModel> getAllProducts() {
        return queryService.getAllProducts();
    }

    // Request DTO
    public static class CreateProductRequest {
        private String name;
        private Double price;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}


