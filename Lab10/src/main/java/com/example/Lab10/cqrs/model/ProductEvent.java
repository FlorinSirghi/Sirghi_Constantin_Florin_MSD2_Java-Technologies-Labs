package com.example.Lab10.cqrs.model;

import java.time.LocalDateTime;

public class ProductEvent {
    private String eventType;
    private Long productId;
    private Product product;
    private LocalDateTime timestamp;

    public ProductEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public ProductEvent(String eventType, Long productId, Product product) {
        this.eventType = eventType;
        this.productId = productId;
        this.product = product;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}




