package com.example.Lab3.controller;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import com.example.Lab3.repository.CustomerRepository;
import com.example.Lab3.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @PostMapping("/process")
    public Map<String, Object> processOrder(@RequestBody Map<String, Object> request) {
        Long customerId = Long.valueOf(request.get("customerId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        Order order = orderService.processOrder(customerId, amount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("discountStrategy", orderService.getCurrentDiscountStrategy());
        response.put("message", "Order processed successfully");
        
        return response;
    }
    
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }
    
    @GetMapping("/discount-strategy")
    public Map<String, String> getCurrentDiscountStrategy() {
        Map<String, String> response = new HashMap<>();
        response.put("strategy", orderService.getCurrentDiscountStrategy());
        response.put("description", getStrategyDescription(orderService.getCurrentDiscountStrategy()));
        return response;
    }
    
    @PostMapping("/test-scenarios")
    public Map<String, Object> testDiscountScenarios() {
        Map<String, Object> results = new HashMap<>();
        
        // Test scenario 1: Loyal customer with small order
        Customer loyalCustomer = customerRepository.findById(1L).orElse(null);
        if (loyalCustomer != null) {
            Order order1 = orderService.processOrder(1L, new BigDecimal("100.00"));
            results.put("loyalCustomerSmallOrder", order1);
        }
        
        // Test scenario 2: Non-loyal customer with large order
        Customer regularCustomer = customerRepository.findById(2L).orElse(null);
        if (regularCustomer != null) {
            Order order2 = orderService.processOrder(2L, new BigDecimal("1500.00"));
            results.put("regularCustomerLargeOrder", order2);
        }
        
        // Test scenario 3: Loyal customer with large order (should trigger event)
        if (loyalCustomer != null) {
            Order order3 = orderService.processOrder(1L, new BigDecimal("2000.00"));
            results.put("loyalCustomerLargeOrder", order3);
        }

        return results;
    }
    
    private String getStrategyDescription(String strategy) {
        switch (strategy) {
            case "LOYAL_CUSTOMER":
                return "15% discount for loyal customers";
            case "LARGE_VALUE":
                return "$100 discount for orders over $1000";
            case "NO_DISCOUNT":
                return "No discount applied";
            default:
                return "Unknown strategy";
        }
    }
}
