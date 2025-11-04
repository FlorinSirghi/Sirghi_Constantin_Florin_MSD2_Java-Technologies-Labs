package com.example.Lab3.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {
    private Long id;
    private String name;
    private String email;
    private boolean isLoyal;
    private LocalDateTime registrationDate;
    
    public Customer() {}
    
    public Customer(Long id, String name, String email, boolean isLoyal) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isLoyal = isLoyal;
        this.registrationDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isLoyal() { return isLoyal; }
    public void setLoyal(boolean loyal) { isLoyal = loyal; }
    
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', email='" + email + "', isLoyal=" + isLoyal + "}";
    }
}
