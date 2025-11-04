package com.example.Lab3.service;

import com.example.Lab3.event.LargeDiscountEvent;
import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import com.example.Lab3.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    
    private final DiscountService discountService;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    
    // Threshold for large discount events
    private static final BigDecimal LARGE_DISCOUNT_THRESHOLD = new BigDecimal("200.00");
    
    @Autowired
    public OrderService(DiscountService discountService,
                       CustomerRepository customerRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.discountService = discountService;
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public Order processOrder(Long customerId, BigDecimal amount) {

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Order order = new Order(orderIdGenerator.getAndIncrement(), customerId, amount);

        BigDecimal discountAmount = discountService.calculateDiscount(customer, order);
        order.setDiscountAmount(discountAmount);
        order.setStatus("PROCESSED");

        logDiscountApplication(customer, order, discountAmount);

        if (discountAmount.compareTo(LARGE_DISCOUNT_THRESHOLD) > 0) {
            eventPublisher.publishEvent(new LargeDiscountEvent(this, customer, order, discountAmount));
        }
        
        return order;
    }
    
    private void logDiscountApplication(Customer customer, Order order, BigDecimal discountAmount) {
        System.out.println("=== DISCOUNT APPLICATION LOG ===");
        System.out.println("Method: OrderService.processOrder");
        System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
        System.out.println("Discount Type: " + discountService.getDiscountType());
        System.out.println("Discount Amount: $" + discountAmount);
        System.out.println("Original Amount: $" + order.getOriginalAmount());
        System.out.println("Final Amount: $" + order.getFinalAmount());
        System.out.println("================================");
    }
    
    public String getCurrentDiscountStrategy() {
        return discountService.getDiscountType();
    }
}
