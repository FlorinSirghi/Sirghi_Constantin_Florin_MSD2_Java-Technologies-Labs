package com.example.Lab3.event;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class LargeDiscountEvent extends ApplicationEvent {
    
    private final Customer customer;
    private final Order order;
    private final BigDecimal discountAmount;
    
    public LargeDiscountEvent(Object source, Customer customer, Order order, BigDecimal discountAmount) {
        super(source);
        this.customer = customer;
        this.order = order;
        this.discountAmount = discountAmount;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    @Override
    public String toString() {
        return "LargeDiscountEvent{customer=" + customer.getName() + 
               ", orderId=" + order.getId() + 
               ", discountAmount=" + discountAmount + "}";
    }
}
