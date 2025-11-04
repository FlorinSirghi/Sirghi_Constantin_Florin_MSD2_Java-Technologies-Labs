package com.example.Lab3.service;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("noDiscount")
public class NoDiscountService implements DiscountService {
    
    @Override
    public BigDecimal calculateDiscount(Customer customer, Order order) {
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getDiscountType() {
        return "NO_DISCOUNT";
    }
}
