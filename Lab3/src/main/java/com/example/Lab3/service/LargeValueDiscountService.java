package com.example.Lab3.service;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("largeValueDiscount")
public class LargeValueDiscountService implements DiscountService {
    
    private static final BigDecimal LARGE_VALUE_THRESHOLD = new BigDecimal("1000.00");
    private static final BigDecimal LARGE_VALUE_DISCOUNT = new BigDecimal("100.00");
    
    @Override
    public BigDecimal calculateDiscount(Customer customer, Order order) {
        if (order.getOriginalAmount().compareTo(LARGE_VALUE_THRESHOLD) >= 0) {
            return LARGE_VALUE_DISCOUNT;
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getDiscountType() {
        return "LARGE_VALUE";
    }
}
