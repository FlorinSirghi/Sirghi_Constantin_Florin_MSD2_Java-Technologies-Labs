package com.example.Lab10.cqrs.repository;

import com.example.Lab10.cqrs.model.ProductReadModel;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductReadModelRepository {
    
    private final Map<String, ProductReadModel> readModelStore = new HashMap<>();

    public void save(ProductReadModel product) {
        readModelStore.put(product.getId(), product);
    }

    public List<ProductReadModel> findAll() {
        return new ArrayList<>(readModelStore.values());
    }
}


