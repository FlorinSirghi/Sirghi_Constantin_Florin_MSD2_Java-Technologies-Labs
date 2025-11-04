package com.example.Lab3.config;

import com.example.Lab3.service.DiscountService;
import com.example.Lab3.service.LoyalCustomerDiscountService;
import com.example.Lab3.service.LargeValueDiscountService;
import com.example.Lab3.service.NoDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DiscountConfiguration {
    
    @Value("${discount.strategy:loyal}")
    private String discountStrategy;
    
    @Autowired
    private LoyalCustomerDiscountService loyalService;
    
    @Autowired
    private LargeValueDiscountService largeValueService;
    
    @Autowired
    private NoDiscountService noDiscountService;
    
    @Bean
    @Primary
    public DiscountService discountService() {
        System.out.println("=== DISCOUNT CONFIGURATION ===");
        System.out.println("Reading discount strategy from properties: " + discountStrategy);
        
        switch (discountStrategy.toLowerCase()) {
            case "loyal":
            case "loyal-customer":
                System.out.println("Selected: Loyal Customer Discount Strategy");
                return loyalService;
            case "large-value":
            case "largevalue":
                System.out.println("Selected: Large Value Discount Strategy");
                return largeValueService;
            case "none":
            case "no-discount":
                System.out.println("Selected: No Discount Strategy");
                return noDiscountService;
            default:
                System.out.println("Unknown strategy '" + discountStrategy + "', defaulting to Loyal Customer Discount");
                return loyalService;
        }
    }
}
