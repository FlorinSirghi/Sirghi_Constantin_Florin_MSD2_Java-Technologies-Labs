package com.example.Lab3.service;

import com.example.Lab3.model.Customer;
import com.example.Lab3.model.Order;
import java.math.BigDecimal;

public interface DiscountService {
    BigDecimal calculateDiscount(Customer customer, Order order);
    String getDiscountType();
}
