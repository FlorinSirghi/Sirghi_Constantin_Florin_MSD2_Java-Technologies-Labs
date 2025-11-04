package com.example.Lab3.repository;

import com.example.Lab3.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerRepository {
    
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public CustomerRepository() {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        save(new Customer(null, "John Doe", "john.doe@email.com", true));
        save(new Customer(null, "Jane Smith", "jane.smith@email.com", false));
        save(new Customer(null, "Bob Johnson", "bob.johnson@email.com", true));
        save(new Customer(null, "Alice Brown", "alice.brown@email.com", false));
        save(new Customer(null, "Charlie Wilson", "charlie.wilson@email.com", true));
    }
    
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        customers.put(customer.getId(), customer);
        return customer;
    }
    
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(customers.get(id));
    }
    
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }
    
    public boolean existsById(Long id) {
        return customers.containsKey(id);
    }
    
    public void deleteById(Long id) {
        customers.remove(id);
    }
    
    public long count() {
        return customers.size();
    }
}
