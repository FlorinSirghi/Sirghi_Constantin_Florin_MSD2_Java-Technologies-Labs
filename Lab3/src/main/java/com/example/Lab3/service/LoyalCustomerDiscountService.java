package com.example.Lab3.service;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("loyalCustomerDiscount")
public class LoyalCustomerDiscountService implements DiscountService {
    
    private static final BigDecimal LOYAL_DISCOUNT_PERCENTAGE = new BigDecimal("0.15"); // 15%
    
    @Override
    public BigDecimal calculateDiscount(Customer customer, Order order) {
        if (customer.isLoyal()) {
            return order.getOriginalAmount().multiply(LOYAL_DISCOUNT_PERCENTAGE);
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getDiscountType() {
        return "LOYAL_CUSTOMER";
    }
}
