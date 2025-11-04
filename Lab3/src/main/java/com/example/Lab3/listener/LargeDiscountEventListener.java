package com.example.Lab3.listener;

import com.example.Lab3.event.LargeDiscountEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LargeDiscountEventListener {
    
    @EventListener
    public void handleLargeDiscountEvent(LargeDiscountEvent event) {
        System.out.println("\n=== LARGE DISCOUNT EVENT LISTENER ===");
        System.out.println("ALERT: Large discount applied!");
        System.out.println("Customer: " + event.getCustomer().getName());
        System.out.println("Order ID: " + event.getOrder().getId());
        System.out.println("Discount Amount: $" + event.getDiscountAmount());
        System.out.println("Original Amount: $" + event.getOrder().getOriginalAmount());
        System.out.println("Final Amount: $" + event.getOrder().getFinalAmount());
        System.out.println("Event Time: " + java.time.LocalDateTime.now());
        System.out.println("=====================================\n");
    }
}
