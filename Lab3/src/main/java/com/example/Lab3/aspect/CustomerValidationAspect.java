package com.example.Lab3.aspect;

import com.example.Lab3.exception.CustomerNotEligibleException;
import com.example.Lab3.model.Customer;
import com.example.Lab3.repository.CustomerRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CustomerValidationAspect {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Before("execution(* com.example.Lab3.service.OrderService.processOrder(..))")
    public void validateCustomerBeforeDiscount(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long customerId = (Long) args[0];
        
        System.out.println("=== CUSTOMER VALIDATION ASPECT ===");
        System.out.println("Validating customer before applying discount...");

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotEligibleException("Customer not found with ID: " + customerId));

        if (!isCustomerEligibleForDiscount(customer)) {
            throw new CustomerNotEligibleException("Customer " + customer.getName() + " is not eligible for discounts");
        }
        
        System.out.println("Customer validation passed: " + customer.getName());
        System.out.println("Customer is eligible for discounts: " + customer.isLoyal());
        System.out.println("==================================");
    }
    
    private boolean isCustomerEligibleForDiscount(Customer customer) {
        return true;
    }
}
