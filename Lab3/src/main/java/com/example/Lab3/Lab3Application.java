package com.example.Lab3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Lab3Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Lab3Application.class, args);

		System.out.println("GET  http://localhost:8080/api/orders/customers - List all customers");
		System.out.println("GET  http://localhost:8080/api/orders/customers/{id} - Get customer by ID");
		System.out.println("GET  http://localhost:8080/api/orders/discount-strategy - Get current discount strategy");
		System.out.println("POST http://localhost:8080/api/orders/test-scenarios - Run test scenarios");
	}

}
